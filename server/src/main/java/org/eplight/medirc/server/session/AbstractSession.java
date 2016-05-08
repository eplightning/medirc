package org.eplight.medirc.server.session;

import com.google.protobuf.MessageOrBuilder;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.protocol.SessionUserFlag;
import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;

import java.util.*;

abstract public class AbstractSession implements Session {

    protected int id;
    protected String name;
    protected User owner;
    protected SessionState state;

    protected Set<User> participants = new HashSet<>();
    protected Map<Integer, EnumSet<SessionUserFlag>> flags = new HashMap<>();

    @Override
    public Main.Session buildMessage(User currentUser) {
        Main.Session.Builder builder = Main.Session.newBuilder();

        builder.setName(name)
                .setId(id)
                .setOwner(owner.buildUserMessage())
                .setUsers(getActiveUsers().size())
                .setOwnership(currentUser != null && currentUser.equals(owner))
                .setState(state.toProtobuf());

        return builder.build();
    }

    @Override
    public SessionBasic.SessionData buildDataMessage() {
        SessionBasic.SessionData.Builder builder = SessionBasic.SessionData.newBuilder();

        builder.setName(name)
                .setState(state.toProtobuf());

        return builder.build();
    }

    @Override
    public Set<User> getParticipants() {
        return participants;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isAllowedToJoin(User user) {
        return state != SessionState.Finished &&
                (getOwner().equals(user) || (getParticipants().contains(user) && state == SessionState.Started));
    }

    @Override
    public boolean isAdmin(User user) {
        return getOwner().equals(user);
    }

    @Override
    public void broadcast(MessageOrBuilder msg, User except) {
        for (ActiveUser user : getActiveUsers()) {
            if (!user.equals(except)) {
                user.getChannel().writeAndFlush(msg);
            }
        }
    }

    @Override
    public void broadcast(MessageOrBuilder msg) {
        for (ActiveUser user : getActiveUsers()) {
            user.getChannel().writeAndFlush(msg);
        }
    }

    @Override
    public EnumSet<SessionUserFlag> getFlags(User user) {
        EnumSet<SessionUserFlag> res = flags.get(user.getId());

        return res != null ? res : EnumSet.noneOf(SessionUserFlag.class);
    }

    @Override
    public SessionState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Session)) return false;

        Session that = (Session) o;

        return id == that.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }
}
