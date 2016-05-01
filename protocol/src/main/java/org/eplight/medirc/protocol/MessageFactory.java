package org.eplight.medirc.protocol;

import com.google.protobuf.Parser;
import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageFactory {

    private final static HashMap<Short, Parser> mapping;
    private final static HashMap<Class, Short> reverseMapping;

    static {
        HashMap<Short, Message> tmpMap = new HashMap<>();

        // id to message mapping
        tmpMap.put((short) 1, Basic.Heartbeat.getDefaultInstance());
        tmpMap.put((short) 2, Basic.Handshake.getDefaultInstance());
        tmpMap.put((short) 3, Basic.HandshakeAck.getDefaultInstance());
        tmpMap.put((short) 4, Main.ActiveSessions.getDefaultInstance());
        tmpMap.put((short) 5, Main.SyncRequest.getDefaultInstance());
        tmpMap.put((short) 6, Main.ArchivedSessions.getDefaultInstance());
        tmpMap.put((short) 7, Main.UserList.getDefaultInstance());
        tmpMap.put((short) 8, Main.CreateNewSession.getDefaultInstance());
        tmpMap.put((short) 9, Main.NewSessionResponse.getDefaultInstance());
        tmpMap.put((short) 10, Main.SessionInvite.getDefaultInstance());
        tmpMap.put((short) 11, Main.SessionClosed.getDefaultInstance());
        tmpMap.put((short) 12, Main.UserConnected.getDefaultInstance());
        tmpMap.put((short) 13, Main.UserDisconnected.getDefaultInstance());

        mapping = new HashMap<>();
        reverseMapping = new HashMap<>();

        for (Map.Entry<Short, Message> item : tmpMap.entrySet()) {
            mapping.put(item.getKey(), item.getValue().getParserForType());
            reverseMapping.put(item.getValue().getClass(), item.getKey());
        }
    }

    public static Parser getParser(short id) {
        return mapping.get(id);
    }

    public static short getId(Class clazz) {
        return reverseMapping.get(clazz);
    }
}
