package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.SessionUserFlag;
import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class MemorySession extends AbstractSession {

    protected Set<ActiveUser> activeUsers;

    public MemorySession(int id, String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.state = SessionState.SettingUp;
        this.id = id;
        this.activeUsers = new HashSet<>();

        this.flags.put(owner.getId(), EnumSet.of(SessionUserFlag.Owner));
    }

    @Override
    public Set<ActiveUser> getActiveUsers() {
        return activeUsers;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setFlags(User user, EnumSet<SessionUserFlag> flags) {
        this.flags.put(user.getId(), flags);
    }

    @Override
    public void setState(SessionState state) {
        this.state = state;
    }

    @Override
    public void join(ActiveUser user) {
        activeUsers.add(user);
    }

    @Override
    public void part(ActiveUser user) {
        activeUsers.remove(user);
    }

    @Override
    public void invite(User user) {
        if (getOwner().equals(user))
            return;

        participants.add(user);

        flags.put(user.getId(), EnumSet.of(SessionUserFlag.Invited));
    }

    @Override
    public void kick(User user) {
        participants.remove(user);
    }
}
