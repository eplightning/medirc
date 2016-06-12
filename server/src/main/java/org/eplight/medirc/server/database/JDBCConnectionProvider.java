package org.eplight.medirc.server.database;

import com.google.inject.Provider;
import org.eplight.medirc.server.config.ConfigurationManager;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCConnectionProvider implements Provider<Connection> {

    @Inject
    private ConfigurationManager configurationManager;

    @Override
    public Connection get() {
        try {
            Class.forName(configurationManager.getString("database.driver"));
        } catch (ClassNotFoundException e) {
            // TODO: Logowanie
            return null;
        }

        Connection c;

        try {
            c = DriverManager.getConnection(configurationManager.getString("database.uri"));

            c.setAutoCommit(true);

            // SQLite'owe
            if (configurationManager.getString("database.driver").equals("org.sqlite.JDBC")) {
                if (configurationManager.getBool("database.autocreate"))
                    createTables(c);

                c.createStatement().execute("PRAGMA foreign_keys = ON");
            }
        } catch (SQLException e) {
            // TODO: Logowanie
            return null;
        }

        return c;
    }

    private void createTables(Connection c) throws SQLException {
        Statement stmt = c.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS `users` (\n" +
                "\t`id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`name`\tTEXT NOT NULL UNIQUE,\n" +
                "\t`hash`\tTEXT,\n" +
                "\t`salt`\tTEXT\n" +
                ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS `sessions` (\n" +
                "\t`id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`name`\tTEXT NOT NULL,\n" +
                "\t`state`\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\t`auto_voice`\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\t`owner_id`\tINTEGER NOT NULL,\n" +
                "\t`owner_flags`\tINTEGER NOT NULL,\n" +
                "\tFOREIGN KEY(`owner_id`) REFERENCES users (id)\n" +
                ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS `session_users` (\n" +
                "\t`session_id`\tINTEGER NOT NULL,\n" +
                "\t`user_id`\tINTEGER NOT NULL,\n" +
                "\t`flags`\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\tPRIMARY KEY(session_id,user_id),\n" +
                "\tFOREIGN KEY(`session_id`) REFERENCES sessions (id),\n" +
                "\tFOREIGN KEY(`user_id`) REFERENCES users (id)\n" +
                ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS `images` (\n" +
                "\t`id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`session_id`\tINTEGER NOT NULL,\n" +
                "\t`is_archived`\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\t`name`\tTEXT NOT NULL,\n" +
                "\t`data`\tBLOB NOT NULL,\n" +
                "\t`width`\tINTEGER NOT NULL,\n" +
                "\t`height`\tINTEGER NOT NULL,\n" +
                "\t`color_red`\tREAL NOT NULL,\n" +
                "\t`color_green`\tREAL NOT NULL,\n" +
                "\t`color_blue`\tREAL NOT NULL,\n" +
                "\t`zoom`\tREAL NOT NULL,\n" +
                "\t`focus_x`\tINTEGER NOT NULL,\n" +
                "\t`focus_y`\tINTEGER NOT NULL,\n" +
                "\tFOREIGN KEY(`session_id`) REFERENCES `sessions`(`id`)\n" +
                ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS `image_fragments` (\n" +
                "\t`id`\tINTEGER NOT NULL,\n" +
                "\t`user_id`\tINTEGER NOT NULL,\n" +
                "\t`image_id`\tINTEGER NOT NULL,\n" +
                "\t`type`\tTEXT NOT NULL DEFAULT 'rect',\n" +
                "\t`x1`\tINTEGER NOT NULL,\n" +
                "\t`x2`\tINTEGER NOT NULL,\n" +
                "\t`y1`\tINTEGER NOT NULL,\n" +
                "\t`y2`\tINTEGER NOT NULL,\n" +
                "\t`zoom`\tREAL NOT NULL,\n" +
                "\t`color_red`\tREAL NOT NULL,\n" +
                "\t`color_green`\tREAL NOT NULL,\n" +
                "\t`color_blue`\tREAL NOT NULL,\n" +
                "\tPRIMARY KEY(id,image_id),\n" +
                "\tFOREIGN KEY(`user_id`) REFERENCES `users`(`id`),\n" +
                "\tFOREIGN KEY(`image_id`) REFERENCES `images`(`id`)\n" +
                ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS `session_events` (\n" +
                "\t`id`\tINTEGER NOT NULL,\n" +
                "\t`session_id`\tINTEGER NOT NULL,\n" +
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
                "\tPRIMARY KEY(id),\n" +
                "\tFOREIGN KEY(`session_id`) REFERENCES `sessions`(`id`),\n" +
                "\tFOREIGN KEY(`image_id`) REFERENCES `images`(`id`)\n" +
                ")");
    }
}
