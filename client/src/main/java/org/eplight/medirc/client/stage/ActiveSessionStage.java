package org.eplight.medirc.client.stage;

import javafx.stage.WindowEvent;
import org.eplight.medirc.client.data.SessionUser;
import org.eplight.medirc.client.instance.network.Connection;
import org.eplight.medirc.client.instance.network.dispatcher.JavaFxDispatchFunction;
import org.eplight.medirc.client.instance.network.dispatcher.MessageDispatcher;
import org.eplight.medirc.protocol.*;

import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Created by EpLightning on 08.05.2016.
 */
public class ActiveSessionStage extends AbstractSessionStage {

    private Connection connection;
    private MessageDispatcher parentDispatcher;
    private MessageDispatcher dispatcher;
    private SessionBasic.SessionData data;
    private int id;
    private EnumSet<SessionUserFlag> yourFlags;

    public ActiveSessionStage(SessionResponses.JoinResponse msg, Connection connection, MessageDispatcher dispatcher,
                              Consumer<AbstractSessionStage> onCloseRun) {
        super(onCloseRun);
        this.connection = connection;
        this.parentDispatcher = dispatcher;
        this.dispatcher = new MessageDispatcher();
        this.parentDispatcher.addChildDispatcher(this.dispatcher);

        this.data = msg.getData();
        this.id = msg.getStatus().getSessionId();
        this.yourFlags = SessionUserFlag.fromProtobuf(msg.getYourFlags());

        setupWindow(msg.getData().getName());
        setupView(msg);
        setupEvents();

        show();
    }

    private void setupEvents() {
        dispatcher.register(SessionEvents.Joined.class, new JavaFxDispatchFunction<>(this::onJoined));
        dispatcher.register(SessionEvents.UserMessage.class, new JavaFxDispatchFunction<>(this::onUserMessage));
        dispatcher.register(SessionEvents.SettingsChanged.class, new JavaFxDispatchFunction<>(this::onSettingsChanged));
    }

    private String getStateButtonText(Main.Session.State state) {
        switch (state) {
            case Started:
                return "Zakończ sesję";
            case SettingUp:
                return "Rozpocznij sesję";
            default:
                return "Zakończona sesja";
        }
    }

    private void setupView(SessionResponses.JoinResponse msg) {
        setButtonsState(getStateButtonText(msg.getData().getState()), yourFlags.contains(SessionUserFlag.Owner));

        msg.getActiveUserList()
                .forEach(a -> addActiveUser(new SessionUser(a)));

        msg.getParticipantList()
                .forEach(a -> addParticipant(new SessionUser(a)));

        // TODO: Images
    }

    private void onJoined(SessionEvents.Joined msg) {
        addActiveUser(new SessionUser(msg.getUser()));
    }

    private void onUserMessage(SessionEvents.UserMessage msg) {
        addMessage(new SessionUser(msg.getUser()), msg.getText());
    }

    private void onSettingsChanged(SessionEvents.SettingsChanged msg) {
        if (!msg.getData().getName().equals(data.getName())) {
            setName(msg.getData().getName());
            addMessage(null, "Nazwa sesji została zmieniona");
        }

        if (!msg.getData().getState().equals(data.getState())) {
            setButtonsState(getStateButtonText(msg.getData().getState()), yourFlags.contains(SessionUserFlag.Owner));
        }

        if (msg.getData().getState() == Main.Session.State.Finished) {
            disableUserInput(false);
            addMessage(null, "Sesja została zakończona");
        } else if (msg.getData().getState() == Main.Session.State.Started) {
            addMessage(null, "Sesja została rozpoczęta");
        }

        data = msg.getData();
    }

    @Override
    protected void onInviteSend(String name) {
        SessionRequests.InviteUser.Builder b = SessionRequests.InviteUser.newBuilder()
                .setSessionId(id)
                .setUserName(name);

        connection.writeAndFlush(b.build());
    }

    @Override
    protected void onCloseRequest(WindowEvent event) {
        connection.writeAndFlush(SessionRequests.PartRequest.newBuilder().setId(id).build());

        parentDispatcher.removeChildDispatcher(dispatcher);
        super.onCloseRequest(event);
    }

    @Override
    protected void onSendMessage(String text) {
        super.onSendMessage(text);

        SessionRequests.SendMessage.Builder msg = SessionRequests.SendMessage.newBuilder()
                .setSessionId(id)
                .setText(text);

        connection.writeAndFlush(msg.build());
    }

    @Override
    protected void onSessionAdvanced() {
        super.onSessionAdvanced();

        Main.Session.State nextState;

        switch (data.getState()) {
            case SettingUp:
                nextState = Main.Session.State.Started;
                break;

            case Started:
                nextState = Main.Session.State.Finished;
                break;

            default:
                return;
        }

        SessionRequests.ChangeSettings.Builder b = SessionRequests.ChangeSettings.newBuilder()
                .setSessionId(id);

        b.setData(data.toBuilder().setState(nextState));

        connection.writeAndFlush(b.build());
    }
}
