package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.Main;

public enum SessionState {
    SettingUp(Main.Session.State.SettingUp, 0),
    Started(Main.Session.State.Started, 1),
    Finished(Main.Session.State.Finished, 2);

    private Main.Session.State protobuf;
    private int number;

    SessionState(Main.Session.State protobuf, int number) {
        this.protobuf = protobuf;
        this.number = number;
    }

    public static SessionState fromProtobuf(Main.Session.State state) {
        switch (state) {
            case Finished:
                return Finished;
            case Started:
                return Started;
            default:
                return SettingUp;
        }
    }

    public static SessionState fromNumber(int number) {
        switch (number) {
            case 0:
                return SettingUp;

            case 1:
                return Started;

            default:
                return Finished;
        }
    }

    public int getNumber() {
        return number;
    }

    public Main.Session.State toProtobuf() {
        return protobuf;
    }
}
