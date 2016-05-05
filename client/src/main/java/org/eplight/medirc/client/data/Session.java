package org.eplight.medirc.client.data;

import org.eplight.medirc.protocol.Main;

public class Session {

    private Main.Session sessionMsg;

    public Session(Main.Session sessionMsg) {
        this.sessionMsg = sessionMsg;
    }

    public String getName() {
        return sessionMsg.getName();
    }

    public int getUsersCount() {
        return sessionMsg.getUsers();
    }

    public boolean isOwner() {
        return sessionMsg.getOwnership();
    }

    public boolean isStarted() {
        return sessionMsg.getState() == Main.Session.State.Started;
    }
}
