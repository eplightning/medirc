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

    public SessionUser(int id, String name) {
        this.id = id;
        this.name = name;
        this.flags = SessionUserFlag.fromProtobuf(0);
    }

    public SessionUser(int id, String name, EnumSet<SessionUserFlag> flags) {
        this.id = id;
        this.name = name;
        this.flags = flags;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;

        if (!(obj instanceof SessionUser)) return false;

        SessionUser that = (SessionUser) obj;

        return that.getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
