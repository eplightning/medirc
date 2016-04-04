package org.eplight.medirc.server.network;

import io.netty.util.AttributeKey;
import org.eplight.medirc.server.user.User;

public class SocketAttributes {

    public final static AttributeKey<String> PIPELINE_ERROR = AttributeKey.valueOf("pipelineException");
    public final static AttributeKey<User> USER_OBJECT = AttributeKey.valueOf("userObj");
    public final static AttributeKey<Long> LAST_ACTIVITY = AttributeKey.valueOf("lastActivity");
}
