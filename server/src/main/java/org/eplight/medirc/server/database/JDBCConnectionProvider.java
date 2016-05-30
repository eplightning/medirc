package org.eplight.medirc.server.database;

import com.google.inject.Provider;
import org.eplight.medirc.server.config.ConfigurationManager;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
                // PRAGMA foreign_keys = ON;

                if (configurationManager.getBool("database.autocreate"))
                    createTables();
            }
        } catch (SQLException e) {
            // TODO: Logowanie
            return null;
        }

        return c;
    }

    private void createTables() throws SQLException {

    }
}
