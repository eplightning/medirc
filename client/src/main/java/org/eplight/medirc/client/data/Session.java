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

    public boolean isInvited() {
        return sessionMsg.getInvited();
    }

    public boolean isStarted() {
        return sessionMsg.getState() == Main.Session.State.Started;
    }

    public boolean isArchived() {
        return sessionMsg.getState() == Main.Session.State.Finished;
    }

    public int getId() {
        return sessionMsg.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (!(obj instanceof Session)) return false;

        Session that = (Session) obj;

        return that.getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
