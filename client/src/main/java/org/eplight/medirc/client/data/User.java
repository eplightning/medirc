package org.eplight.medirc.client.data;

import org.eplight.medirc.protocol.Main;

public class User {
    private Main.User userMsg;

    public User(Main.User userMsg) {
        this.userMsg = userMsg;
    }

    public String getName() {
        return userMsg.getName();
    }

    public String toString(){
        return this.getName();
    }

    public int getId() {
        return userMsg.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (!(obj instanceof User)) return false;

        User that = (User) obj;

        return that.getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
