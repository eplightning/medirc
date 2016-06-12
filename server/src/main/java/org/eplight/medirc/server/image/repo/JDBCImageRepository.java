package org.eplight.medirc.server.image.repo;

import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.image.ImageColor;
import org.eplight.medirc.server.image.ImageTransformations;
import org.eplight.medirc.server.image.JDBCImage;
import org.eplight.medirc.server.image.fragments.ImageFragment;
import org.eplight.medirc.server.image.fragments.RectImageFragment;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.factory.UserRepository;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by EpLightning on 12.06.2016.
 */
public class JDBCImageRepository implements ImageRepository {

    @Inject
    private Connection connection;

    @Inject
    private UserRepository userRepository;

    @Override
    public Image create(int session) {
        return new JDBCImage(session, this);
    }

    @Override
    public Image findById(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM images WHERE id = ?" +
                    " AND is_archived = 0");

            stmt.setInt(1, id);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                return createImage(set);
            }
        } catch (SQLException e) {
            // logger
        }

        return null;
    }

    @Override
    public Image findById(int id, boolean includeHidden) {
        if (!includeHidden)
            findById(id);

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM images WHERE id = ?");

            stmt.setInt(1, id);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                return createImage(set);
            }
        } catch (SQLException e) {
            // logger
        }

        return null;
    }

    @Override
    public Set<Image> findSessionImages(int sess) {
        HashSet<Image> result = new HashSet<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM images WHERE session_id = ?" +
                    " AND is_archived = 0");

            stmt.setInt(1, sess);

            ResultSet set = stmt.executeQuery();

            while (set.next()) {
                result.add(createImage(set));
            }

            return result;
        } catch (SQLException e) {
            // logger
        }

        return new HashSet<>();
    }

    @Override
    public Set<Image> findSessionImages(int sess, boolean includeHidden) {
        if (!includeHidden)
            return findSessionImages(sess);

        HashSet<Image> result = new HashSet<>();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM images WHERE session_id = ?");

            stmt.setInt(1, sess);

            ResultSet set = stmt.executeQuery();

            while (set.next()) {
                result.add(createImage(set));
            }

            return result;
        } catch (SQLException e) {
            // logger
        }

        return new HashSet<>();
    }

    @Override
    public void persist(Image img) throws ImageRepositoryException {
        if (!(img instanceof JDBCImage))
            return;

        JDBCImage jdbcImage = (JDBCImage) img;

        if (jdbcImage.getId() != -1)
            return;

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO images (session_id, name, " +
                    "data, width, height, color_red, color_green, color_blue, zoom, focus_x, focus_y) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, jdbcImage.getSessionId());
            stmt.setString(2, jdbcImage.getName());
            stmt.setBytes(3, jdbcImage.getData());
            stmt.setInt(4, jdbcImage.getWidth());
            stmt.setInt(5, jdbcImage.getHeight());
            stmt.setDouble(6, jdbcImage.getColor().getR());
            stmt.setDouble(7, jdbcImage.getColor().getG());
            stmt.setDouble(8, jdbcImage.getColor().getB());
            stmt.setDouble(9, jdbcImage.getTransformations().getZoom());
            stmt.setInt(10, jdbcImage.getTransformations().getFocusX());
            stmt.setInt(11, jdbcImage.getTransformations().getFocusY());

            stmt.execute();

            // TODO: Persist fragments

            ResultSet generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                jdbcImage.persisted(generatedKeys.getInt(1));
            } else {
                throw new ImageRepositoryException("Persist couldn't retrieve generated key");
            }
        } catch (SQLException e) {
            throw new ImageRepositoryException(e.getMessage());
        }
    }

    @Override
    public void remove(Image img) {
        if (img.getId() == -1)
            return;

        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE images SET is_archived = 1 WHERE id = ?");

            stmt.setInt(1, img.getId());

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }

    private JDBCImage createImage(ResultSet set) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM image_fragments WHERE image_id = ?");

        stmt.setInt(1, set.getInt("id"));

        ResultSet fragSet = stmt.executeQuery();

        ArrayList<ImageFragment> fragments = new ArrayList<>();

        while (fragSet.next()) {
            User owner = userRepository.findById(fragSet.getInt("user_id"));

            if (owner == null)
                continue;

            if (!fragSet.getString("type").equals("rect"))
                continue;

            RectImageFragment fragment = new RectImageFragment(fragSet.getInt("id"), owner);

            fragment.setColor(new ImageColor(fragSet.getDouble("color_red"), fragSet.getDouble("color_green"),
                    fragSet.getDouble("color_blue")));

            fragment.setX1(fragSet.getInt("x1"));
            fragment.setX2(fragSet.getInt("x2"));
            fragment.setY1(fragSet.getInt("y1"));
            fragment.setY2(fragSet.getInt("y2"));
            fragment.setZoom(fragSet.getDouble("zoom"));

            fragments.add(fragment);
        }

        return new JDBCImage(set, fragments, this);
    }

    public void setData(int id, byte[] data, int width, int height) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE images SET data = ?, width = ?, height = ?" +
                    " WHERE id = ?");

            stmt.setBytes(1, data);
            stmt.setInt(2, width);
            stmt.setInt(3, height);
            stmt.setInt(4, id);

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }

    public void setName(int id, String name) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE images SET name = ? WHERE id = ?");

            stmt.setString(1, name);
            stmt.setInt(2, id);

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }

    public void setColor(int id, ImageColor c) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE images SET color_red = ?, color_green = ?," +
                    " color_blue = ? WHERE id = ?");

            stmt.setDouble(1, c.getR());
            stmt.setDouble(2, c.getG());
            stmt.setDouble(3, c.getB());
            stmt.setInt(4, id);

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }

    public void setTransformations(int id, ImageTransformations c) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE images SET zoom = ?, focus_x = ?," +
                    " focus_y = ? WHERE id = ?");

            stmt.setDouble(1, c.getZoom());
            stmt.setInt(2, c.getFocusX());
            stmt.setInt(3, c.getFocusY());
            stmt.setInt(4, id);

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }

    public void addFragment(int id, ImageFragment fragment) {
        if (!(fragment instanceof RectImageFragment))
            return;

        RectImageFragment rectImageFragment = (RectImageFragment) fragment;

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO image_fragments (id, user_id, image_id, " +
                    "type, x1, x2, y1, y2, zoom, color_red, color_green, color_blue) VALUES (?, ?, ?, 'rect', ?, ?, " +
                    "?, ?, ?, ?, ?, ?)");

            stmt.setInt(1, rectImageFragment.getId());
            stmt.setInt(2, rectImageFragment.getUser().getId());
            stmt.setInt(3, id);
            stmt.setInt(4, rectImageFragment.getX1());
            stmt.setInt(5, rectImageFragment.getX2());
            stmt.setInt(6, rectImageFragment.getY1());
            stmt.setInt(7, rectImageFragment.getY2());
            stmt.setDouble(8, rectImageFragment.getZoom());
            stmt.setDouble(9, rectImageFragment.getColor().getR());
            stmt.setDouble(10, rectImageFragment.getColor().getG());
            stmt.setDouble(11, rectImageFragment.getColor().getB());

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }

    public void clearAllFragments(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM image_fragments WHERE image_id = ?");

            stmt.setInt(1, id);

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }

    public void clearUserFragments(int id, int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM image_fragments WHERE image_id = ? AND " +
                    "user_id = ?");

            stmt.setInt(1, id);
            stmt.setInt(2, userId);

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }
}
