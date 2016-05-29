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
        tmpMap.put((short) 11, Main.SessionUpdated.getDefaultInstance());
        tmpMap.put((short) 12, Main.UserConnected.getDefaultInstance());
        tmpMap.put((short) 13, Main.UserDisconnected.getDefaultInstance());
        tmpMap.put((short) 14, Main.SessionKicked.getDefaultInstance());
        tmpMap.put((short) 15, Main.UserAutocomplete.getDefaultInstance());
        tmpMap.put((short) 16, Main.UserAutocompleteResponse.getDefaultInstance());
        tmpMap.put((short) 17, SessionRequests.JoinRequest.getDefaultInstance());
        tmpMap.put((short) 18, SessionResponses.JoinResponse.getDefaultInstance());
        tmpMap.put((short) 19, SessionRequests.PartRequest.getDefaultInstance());
        tmpMap.put((short) 20, SessionRequests.ChangeSettings.getDefaultInstance());
        tmpMap.put((short) 21, SessionResponses.ChangeSettingsResponse.getDefaultInstance());
        tmpMap.put((short) 22, SessionRequests.InviteUser.getDefaultInstance());
        tmpMap.put((short) 23, SessionResponses.InviteUserResponse.getDefaultInstance());
        tmpMap.put((short) 24, SessionRequests.KickUser.getDefaultInstance());
        tmpMap.put((short) 25, SessionResponses.KickUserResponse.getDefaultInstance());
        tmpMap.put((short) 26, SessionRequests.ChangeUserFlags.getDefaultInstance());
        tmpMap.put((short) 27, SessionResponses.ChangeUserFlagsResponse.getDefaultInstance());
        tmpMap.put((short) 28, SessionRequests.UploadImage.getDefaultInstance());
        tmpMap.put((short) 29, SessionResponses.UploadImageResponse.getDefaultInstance());
        tmpMap.put((short) 30, SessionRequests.RemoveImage.getDefaultInstance());
        tmpMap.put((short) 31, SessionResponses.RemoveImageResponse.getDefaultInstance());
        tmpMap.put((short) 32, SessionRequests.SendMessage.getDefaultInstance());
        tmpMap.put((short) 33, SessionRequests.RequestImage.getDefaultInstance());
        tmpMap.put((short) 34, SessionResponses.RequestImageResponse.getDefaultInstance());
        tmpMap.put((short) 35, SessionEvents.UserMessage.getDefaultInstance());
        tmpMap.put((short) 36, SessionEvents.ServerMessage.getDefaultInstance());
        tmpMap.put((short) 37, SessionEvents.Joined.getDefaultInstance());
        tmpMap.put((short) 38, SessionEvents.Parted.getDefaultInstance());
        tmpMap.put((short) 39, SessionEvents.NewParticipant.getDefaultInstance());
        tmpMap.put((short) 40, SessionEvents.Kicked.getDefaultInstance());
        tmpMap.put((short) 41, SessionEvents.ImageAdded.getDefaultInstance());
        tmpMap.put((short) 42, SessionEvents.ImageRemoved.getDefaultInstance());
        tmpMap.put((short) 43, SessionEvents.SettingsChanged.getDefaultInstance());
        tmpMap.put((short) 44, SessionResponses.TransformImageResponse.getDefaultInstance());
        tmpMap.put((short) 45, SessionEvents.ImageTransformed.getDefaultInstance());
        tmpMap.put((short) 46, SessionRequests.TransformImage.getDefaultInstance());
        tmpMap.put((short) 47, SessionRequests.ZoomImage.getDefaultInstance());

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
