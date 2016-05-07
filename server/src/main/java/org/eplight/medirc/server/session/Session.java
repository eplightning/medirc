package org.eplight.medirc.server.session;

import com.google.protobuf.MessageOrBuilder;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;

import java.util.EnumSet;
import java.util.Set;

public interface Session {

    Main.Session buildMessage(User currentUser);
    SessionBasic.SessionData buildDataMessage();

    Set<ActiveUser> getActiveUsers();
    void join(ActiveUser user);
    void part(ActiveUser user);

    User getOwner();

    Set<User> getParticipants();
    void invite(User user);
    void kick(User user);
    boolean isAllowedToJoin(User user);
    boolean isAdmin(User user);

    void setName(String name);

    void broadcast(MessageOrBuilder msg, User except);
    void broadcast(MessageOrBuilder msg);

    String getName();
    int getId();
    SessionState getState();

    EnumSet<SessionUserFlag> getFlags(User user);
    void setFlags(User user, EnumSet<SessionUserFlag> flags);
    void setState(SessionState state);
}
