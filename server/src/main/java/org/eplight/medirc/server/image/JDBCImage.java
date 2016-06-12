package org.eplight.medirc.server.image;

import org.eplight.medirc.server.image.fragments.ImageFragment;
import org.eplight.medirc.server.image.repo.JDBCImageRepository;
import org.eplight.medirc.server.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by EpLightning on 12.06.2016.
 */
public class JDBCImage extends MemoryImage {

    private JDBCImageRepository repository;

    public JDBCImage(int sessionId, JDBCImageRepository repository) {
        super(sessionId, -1);
        this.repository = repository;
    }

    public JDBCImage(ResultSet set, List<ImageFragment> fragments, JDBCImageRepository repository) throws SQLException {
        super(set.getInt("session_id"), set.getInt("id"));

        this.repository = repository;
        this.width = set.getInt("width");
        this.height = set.getInt("height");
        this.data = set.getBytes("data");
        this.color = new ImageColor(set.getDouble("color_red"), set.getDouble("color_green"),
                set.getDouble("color_blue"));
        this.name = set.getString("name");
        this.transformations = new ImageTransformations(set.getDouble("zoom"), set.getInt("focus_x"),
                set.getInt("focus_y"));
        this.imageFragments = fragments;
    }

    @Override
    public void setData(byte[] data, int width, int height) {
        super.setData(data, width, height);

        if (id != -1)
            repository.setData(id ,data, width, height);
    }

    @Override
    public void setName(String name) {
        super.setName(name);

        if (id != -1)
            repository.setName(id, name);
    }

    @Override
    public void setColor(ImageColor color) {
        super.setColor(color);

        if (id != -1)
            repository.setColor(id, color);
    }

    @Override
    public void setTransformations(ImageTransformations transformations) {
        super.setTransformations(transformations);

        if (id != -1)
            repository.setTransformations(id, transformations);
    }

    @Override
    public void addFragment(ImageFragment frag) {
        super.addFragment(frag);

        if (id != -1)
            repository.addFragment(id, frag);
    }

    @Override
    public void clearFragments(User user) {
        super.clearFragments(user);

        if (id != -1) {
            if (user == null) {
                repository.clearAllFragments(id);
            } else {
                repository.clearUserFragments(id, user.getId());
            }
        }
    }

    public void persisted(int id) {
        this.id = id;
    }
}
