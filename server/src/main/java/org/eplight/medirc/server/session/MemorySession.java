package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.SessionUserFlag;
import org.eplight.medirc.server.user.User;

import java.util.EnumSet;

public class MemorySession extends AbstractSession {

    public MemorySession(int id, String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.state = SessionState.SettingUp;
        this.id = id;

        this.flags.put(owner.getId(), EnumSet.of(SessionUserFlag.Owner));
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

    @Override
    public void setAutoVoice(boolean setting) {
        autoVoice = setting;
    }
}
