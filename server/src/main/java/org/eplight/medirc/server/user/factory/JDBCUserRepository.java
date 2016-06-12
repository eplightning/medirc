package org.eplight.medirc.server.user.factory;

import org.eplight.medirc.server.user.JDBCUser;
import org.eplight.medirc.server.user.User;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by EpLightning on 11.06.2016.
 */
public class JDBCUserRepository implements UserRepository {

    @Inject
    private Connection connection;

    private HashMap<Integer, JDBCUser> cache = new HashMap<>();

    @Override
    public User findById(int id) {
        if (cache.containsKey(id))
            return cache.get(id);

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?");

            stmt.setInt(1, id);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                JDBCUser usr = new JDBCUser(set, this);

                cache.put(usr.getId(), usr);

                return usr;
            }
        } catch (SQLException e) {
            // logger
        }

        return null;
    }

    @Override
    public User findByName(String name) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE name = ?");

            stmt.setString(1, name);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                return new JDBCUser(set, this);
            }
        } catch (SQLException e) {
            // logger
        }

        return null;
    }

    public void setName(int id, String name) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE users SET name = ? WHERE id = ?");

            stmt.setString(1, name);
            stmt.setInt(2, id);

            stmt.execute();
        } catch (SQLException e) {
            // logger
        }
    }
}
