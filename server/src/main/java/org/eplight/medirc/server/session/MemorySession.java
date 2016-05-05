package org.eplight.medirc.server.session;

import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;

import java.util.HashSet;
import java.util.Set;

public class MemorySession extends AbstractSession {

    protected Set<ActiveUser> activeUsers;

    public MemorySession(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.state = SessionState.SettingUp;
        this.id = 0;
        this.activeUsers = new HashSet<>();
    }

    @Override
    public Set<ActiveUser> getActiveUsers() {
        return activeUsers;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
