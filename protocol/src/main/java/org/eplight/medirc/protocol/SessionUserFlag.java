package org.eplight.medirc.protocol;

import java.util.EnumSet;

/**
 * Created by EpLightning on 06.05.2016.
 */
public enum SessionUserFlag {
    Owner(SessionBasic.UserFlags.Owner_VALUE),
    Operator(SessionBasic.UserFlags.Operator_VALUE),
    Voice(SessionBasic.UserFlags.Voice_VALUE),
    Invited(SessionBasic.UserFlags.Invited_VALUE);

    private int value;

    SessionUserFlag(int protobuf) {
        this.value = protobuf;
    }

    public int getValue() {
        return value;
    }

    public static EnumSet<SessionUserFlag> fromProtobuf(int bitset) {
        EnumSet<SessionUserFlag> result = EnumSet.noneOf(SessionUserFlag.class);

        EnumSet.allOf(SessionUserFlag.class).stream()
                .filter(a -> (bitset & a.getValue()) == a.getValue())
                .forEach(result::add);

        return result;
    }

    public static int toProtobuf(EnumSet<SessionUserFlag> set) {
        int bitset = 0;

        for (SessionUserFlag flag : set) {
            bitset |= flag.getValue();
        }

        return bitset;
    }
}
