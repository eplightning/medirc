package org.eplight.medirc.server.session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.image.repo.ImageRepository;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.factory.UserRepository;

import javax.inject.Inject;
import java.io.*;
import java.sql.*;

public class SessionSerializer {

    private static final Logger logger = LogManager.getLogger(SessionSerializer.class);

    @Inject
    private Connection connection;

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private UserRepository userRepository;

    public byte[] serialize(Session session) {
        File tmpFile;

        try {
            tmpFile = File.createTempFile("session-" + session.getId(), ".db");
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }

        try {
            Connection outputConnection = createOutputConnection(tmpFile.getAbsolutePath());

            createRelevantUsers(outputConnection, session);
            createRelevantImages(outputConnection, session);
            copyEvents(outputConnection, session);

            outputConnection.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            tmpFile.delete();
            return null;
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try {
            FileInputStream stream = new FileInputStream(tmpFile);

            byte[] buffer = new byte[4096];

            int read;

            while ((read = stream.read(buffer)) != -1) {
                byteStream.write(buffer, 0, read);
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            tmpFile.delete();
        }

        return byteStream.toByteArray();
    }

    private void createRelevantUsers(Connection outputConnection, Session session) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT user_id FROM session_events WHERE" +
                " session_id = ?");

        stmt.setInt(1, session.getId());

        ResultSet set = stmt.executeQuery();

        PreparedStatement stmt2 = outputConnection.prepareStatement("INSERT INTO users (`id`, `name`)" +
                " VALUES (?, ?)");

        while (set.next()) {
            int id = set.getInt("user_id");

            if (id <= 0)
                continue;

            User u = userRepository.findById(id);

            stmt2.setInt(1, u.getId());
            stmt2.setString(2, u.getName());

            stmt2.execute();
        }
    }

    private void createRelevantImages(Connection outputConnection, Session session) throws SQLException {
        PreparedStatement stmt = outputConnection.prepareStatement("INSERT INTO images (`id`, `name`, `data`, `width`, " +
                "`height`, `color_red`, `color_green`, `color_blue`)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        for (Image img : imageRepository.findSessionImages(session.getId(), true)) {
            stmt.setInt(1, img.getId());
            stmt.setString(2, img.getName());
            stmt.setBytes(3, img.getData());
            stmt.setInt(4, img.getWidth());
            stmt.setInt(5, img.getHeight());
            stmt.setDouble(6, img.getColor().getR());
            stmt.setDouble(7, img.getColor().getG());
            stmt.setDouble(8, img.getColor().getB());

            stmt.execute();
        }
    }

    private void copyEvents(Connection outputConnection, Session session) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM session_events WHERE session_id = ?");
        stmt.setInt(1, session.getId());

        PreparedStatement stmt2 = outputConnection.prepareStatement("INSERT INTO session_events (" +
                "`id`, `timestamp`, `type`, `image_id`, `user_id`, `zoom`, `color_red`, `color_green`, `color_blue`," +
                "`frag_type`, `x1`, `x2`, `y1`, `y2`, `flags`, `reason`, `message`, `state`, `name`, `auto_voice`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ResultSet eventsSet = stmt.executeQuery();

        while (eventsSet.next()) {
            stmt2.setInt(1, eventsSet.getInt("id"));
            stmt2.setString(2, eventsSet.getString("timestamp"));
            stmt2.setString(3, eventsSet.getString("type"));
            copyInt(eventsSet, stmt2, "image_id", 4);
            copyInt(eventsSet, stmt2, "user_id", 5);
            copyReal(eventsSet, stmt2, "zoom", 6);
            copyReal(eventsSet, stmt2, "color_red", 7);
            copyReal(eventsSet, stmt2, "color_green", 8);
            copyReal(eventsSet, stmt2, "color_blue", 9);
            copyString(eventsSet, stmt2, "frag_type", 10);
            copyInt(eventsSet, stmt2, "x1", 11);
            copyInt(eventsSet, stmt2, "x2", 12);
            copyInt(eventsSet, stmt2, "y1", 13);
            copyInt(eventsSet, stmt2, "y2", 14);
            copyInt(eventsSet, stmt2, "flags", 15);
            copyString(eventsSet, stmt2, "reason", 16);
            copyString(eventsSet, stmt2, "message", 17);
            copyInt(eventsSet, stmt2, "state", 18);
            copyString(eventsSet, stmt2, "name", 19);
            copyInt(eventsSet, stmt2, "auto_voice", 20);

            stmt2.execute();
        }
    }

    private void copyInt(ResultSet set, PreparedStatement stmt, String id, int id2) throws SQLException {
        int val = set.getInt(id);

        if (set.wasNull()) {
            stmt.setNull(id2, Types.INTEGER);
        } else {
            stmt.setInt(id2, val);
        }
    }

    private void copyReal(ResultSet set, PreparedStatement stmt, String id, int id2) throws SQLException {
        double val = set.getDouble(id);

        if (set.wasNull()) {
            stmt.setNull(id2, Types.DOUBLE);
        } else {
            stmt.setDouble(id2, val);
        }
    }

    private void copyString(ResultSet set, PreparedStatement stmt, String id, int id2) throws SQLException {
        String val = set.getString(id);

        if (set.wasNull()) {
            stmt.setNull(id2, Types.VARCHAR);
        } else {
            stmt.setString(id2, val);
        }
    }

    private Connection createOutputConnection(String filename) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

        Connection outputConnection;

        outputConnection = DriverManager.getConnection("jdbc:sqlite:"+filename);

        outputConnection.setAutoCommit(true);

        createTables(outputConnection);

        return outputConnection;
    }

    private void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS `users` (\n" +
                "\t`id`\tINTEGER NOT NULL PRIMARY KEY,\n" +
                "\t`name`\tTEXT NOT NULL UNIQUE\n" +
                ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS `images` (\n" +
                "\t`id`\tINTEGER NOT NULL PRIMARY KEY,\n" +
                "\t`name`\tTEXT NOT NULL,\n" +
                "\t`data`\tBLOB NOT NULL,\n" +
                "\t`width`\tINTEGER NOT NULL,\n" +
                "\t`height`\tINTEGER NOT NULL,\n" +
                "\t`color_red`\tREAL NOT NULL,\n" +
                "\t`color_green`\tREAL NOT NULL,\n" +
                "\t`color_blue`\tREAL NOT NULL\n" +
                ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS `session_events` (\n" +
                "\t`id`\tINTEGER NOT NULL PRIMARY KEY,\n" +
                "\t`timestamp`\tTEXT NOT NULL,\n" +
                "\t`type`\tTEXT NOT NULL,\n" +
                "\t`image_id`\tINTEGER,\n" +
                "\t`user_id`\tINTEGER,\n" +
                "\t`zoom`\tREAL,\n" +
                "\t`color_red`\tREAL,\n" +
                "\t`color_green`\tREAL,\n" +
                "\t`color_blue`\tREAL,\n" +
                "\t`frag_type`\tTEXT,\n" +
                "\t`x1`\tINTEGER,\n" +
                "\t`x2`\tINTEGER,\n" +
                "\t`y1`\tINTEGER,\n" +
                "\t`y2`\tINTEGER,\n" +
                "\t`flags`\tINTEGER,\n" +
                "\t`reason`\tTEXT,\n" +
                "\t`message`\tTEXT,\n" +
                "\t`state`\tINTEGER,\n" +
                "\t`name`\tTEXT,\n" +
                "\t`auto_voice`\tINTEGER,\n" +
                "\tFOREIGN KEY(`image_id`) REFERENCES `images`(`id`)\n" +
                ")");
    }
}
