package org.eplight.medirc.server.user;

import org.eplight.medirc.server.user.factory.JDBCUserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by EpLightning on 11.06.2016.
 */
public class JDBCUser extends AbstractUser {

    private JDBCUserRepository connection;

    public JDBCUser(ResultSet set, JDBCUserRepository connection) throws SQLException {
        super(set.getInt("id"), set.getString("name"));

        this.connection = connection;
    }

    @Override
    public void setName(String name) {
        super.setName(name);

        connection.setName(this.id, name);
    }
}
