package org.eplight.medirc.server;

import org.eplight.medirc.protocol.Basic;

public class ServerApplication {

    static public void main(String[] argv)
    {
        System.out.println("Hello world");
        Basic.Heartbeat.Builder b = Basic.Heartbeat.newBuilder();
        Basic.Heartbeat packet = b.build();
        System.out.println(packet.getClass().toString());
    }
}
