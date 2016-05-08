package org.eplight.medirc.server.user;

import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.protocol.SessionUserFlag;

import java.util.EnumSet;

public interface User {
    Main.User buildUserMessage();
    SessionBasic.SessionUser buildSessionUserMessage(EnumSet<SessionUserFlag> flags);

    String getName();

    void setName(String name);

    int getId();
}
