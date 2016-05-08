package org.eplight.medirc.client.data;

import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.protocol.SessionUserFlag;

import java.util.EnumSet;

/**
 * Created by EpLightning on 08.05.2016.
 */
public class SessionUser {

    private int id;
    private String name;
    private EnumSet<SessionUserFlag> flags;

    public SessionUser(SessionBasic.SessionUser user) {
        this.id = user.getId();
        update(user);
    }

    public void update(SessionBasic.SessionUser user) {
        this.name = user.getName();
        this.flags = SessionUserFlag.fromProtobuf(user.getFlags());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EnumSet<SessionUserFlag> getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return name;
    }
}
