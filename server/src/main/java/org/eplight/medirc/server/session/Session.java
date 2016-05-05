package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;

import java.util.Set;

public interface Session {

    Main.Session buildMessage(User currentUser);

    Set<ActiveUser> getActiveUsers();

    User getOwner();

    Set<User> getParticipants();

    void setName(String name);

    String getName();
}
