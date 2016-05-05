package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.Main;

public enum SessionState {
    SettingUp(Main.Session.State.SettingUp),
    Started(Main.Session.State.Started),
    Finished(Main.Session.State.Finished);

    private Main.Session.State protobuf;

    SessionState(Main.Session.State protobuf) {
        this.protobuf = protobuf;
    }

    public Main.Session.State toProtobuf() {
        return protobuf;
    }
}
