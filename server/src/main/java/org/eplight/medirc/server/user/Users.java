package org.eplight.medirc.server.user;

import com.google.protobuf.MessageOrBuilder;
import org.eplight.medirc.server.user.User;

import java.util.HashMap;

/**
 * Created by EpLightning on 28.04.2016.
 */
public class Users extends HashMap<Integer, User> {

    public void broadcast(MessageOrBuilder msg, User except) {
        for (User user : values()) {
            if (!user.equals(except)) {
                user.getChannel().writeAndFlush(msg);
            }
        }
    }

    public void broadcast(MessageOrBuilder msg) {
        for (User user : values()) {
            user.getChannel().writeAndFlush(msg);
        }
    }
}
