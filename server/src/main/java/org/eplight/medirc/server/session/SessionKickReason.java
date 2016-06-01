package org.eplight.medirc.server.session;

import org.eplight.medirc.protocol.SessionEvents;

public enum SessionKickReason {
    Unknown(SessionEvents.Kicked.Reason.Unknown),
    Kick(SessionEvents.Kicked.Reason.Kick),
    Declined(SessionEvents.Kicked.Reason.Declined);

    private SessionEvents.Kicked.Reason protobuf;

    SessionKickReason(SessionEvents.Kicked.Reason protobuf) {
        this.protobuf = protobuf;
    }

    public static SessionKickReason fromProtobuf(SessionEvents.Kicked.Reason protobuf) {
        switch (protobuf) {
            case Kick:
                return Kick;
            case Declined:
                return Declined;
            default:
                return Unknown;
        }
    }

    public SessionEvents.Kicked.Reason toProtobuf() {
        return protobuf;
    }
}
