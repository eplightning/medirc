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

}
