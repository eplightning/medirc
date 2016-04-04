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
