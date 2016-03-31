package org.eplight.medirc.server.user.auth;

import org.eplight.medirc.protocol.Basic;

public interface Authentication {

    public int authenticate(Basic.Handshake msg);
}
