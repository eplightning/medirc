package org.eplight.medirc.server.user.auth;

import org.eplight.medirc.protocol.Basic;

import javax.inject.Inject;
import java.sql.Connection;

/**
 * Created by EpLightning on 27.03.2016.
 */
public class JDBCAuthentication implements Authentication {

    @Inject
    private Connection connection;

    @Override
    public int authenticate(Basic.Handshake msg) {
        if (msg.getSimple().getUsername().isEmpty() || msg.getSimple().getUsername().equals("test"))
            return 1;

        return 2;
    }
}
