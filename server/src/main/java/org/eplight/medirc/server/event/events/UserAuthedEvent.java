package org.eplight.medirc.server.event.events;

import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;

public class UserAuthedEvent implements Event {

    private ActiveUser usr;

    public UserAuthedEvent(ActiveUser usr) {
        this.usr = usr;
    }

    public User getUser() {
        return usr;
    }

}
