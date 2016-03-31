package org.eplight.medirc.server.user.auth;

import org.eplight.medirc.protocol.Basic;

/**
 * Created by EpLightning on 27.03.2016.
 */
public class HardcodedAuthentication implements Authentication {

    @Override
    public int authenticate(Basic.Handshake msg) {
        if (msg.getSimple().getUsername().isEmpty() || msg.getSimple().getUsername().equals("test"))
            return 1;

        return 2;
    }
}
