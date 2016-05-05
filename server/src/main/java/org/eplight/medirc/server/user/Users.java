package org.eplight.medirc.server.user;

import com.google.protobuf.MessageOrBuilder;

import java.util.HashMap;

/**
 * Created by EpLightning on 28.04.2016.
 */
public class Users extends HashMap<Integer, ActiveUser> {

    public void broadcast(MessageOrBuilder msg, User except) {
        for (ActiveUser user : values()) {
            if (!user.equals(except)) {
                user.getChannel().writeAndFlush(msg);
            }
        }
    }

    public void broadcast(MessageOrBuilder msg) {
        for (ActiveUser user : values()) {
            user.getChannel().writeAndFlush(msg);
        }
    }
}
