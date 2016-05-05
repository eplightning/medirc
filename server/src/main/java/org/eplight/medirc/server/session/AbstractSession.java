package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.server.user.User;

import java.util.HashSet;
import java.util.Set;

abstract public class AbstractSession implements Session {

    protected int id;
    protected String name;
    protected User owner;
    protected SessionState state;

    protected Set<User> participants;

    public AbstractSession() {
        this.participants = new HashSet<>();
    }

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
}
