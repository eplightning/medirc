package org.eplight.medirc.server.user;

import org.eplight.medirc.protocol.Main;

public interface User {
    Main.User buildUserMessage();

    String getName();

    void setName(String name);

    int getId();
}
