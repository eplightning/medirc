package org.eplight.medirc.client.stage;

import com.google.protobuf.ByteString;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import org.eplight.medirc.client.data.AllowedActions;
import org.eplight.medirc.client.data.SessionImage;
import org.eplight.medirc.client.data.SessionImageTransformations;
import org.eplight.medirc.client.data.SessionUser;
import org.eplight.medirc.client.instance.network.Connection;
import org.eplight.medirc.client.instance.network.dispatcher.GenericStatusDispatchFunction;
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
                              Consumer<AbstractSessionStage> onCloseRun, Basic.HandshakeAck ack) {
        super(onCloseRun, ack);
        this.connection = connection;
        this.parentDispatcher = dispatcher;
        this.dispatcher = new MessageDispatcher();
        this.parentDispatcher.addChildDispatcher(this.dispatcher);

        this.data = msg.getData();
        this.id = msg.getStatus().getSessionId();
        this.yourFlags = SessionUserFlag.fromProtobuf(msg.getYourFlags());

        setupWindow(msg.getData().getName(), handshakeAck.getName());
        setupView(msg);
        setupEvents();

        show();
    }

    private void setupEvents() {
        dispatcher.register(SessionEvents.Joined.class, new JavaFxDispatchFunction<>(this::onJoined));
        dispatcher.register(SessionEvents.UserMessage.class, new JavaFxDispatchFunction<>(this::onUserMessage));
        dispatcher.register(SessionEvents.SettingsChanged.class, new JavaFxDispatchFunction<>(this::onSettingsChanged));
        dispatcher.register(SessionEvents.NewParticipant.class, new JavaFxDispatchFunction<>(this::onNewParticipant));
        dispatcher.register(SessionEvents.Parted.class, new JavaFxDispatchFunction<>(this::onParted));
        dispatcher.register(SessionEvents.Kicked.class, new JavaFxDispatchFunction<>(this::onKicked));
        dispatcher.register(SessionEvents.UserUpdated.class, new JavaFxDispatchFunction<>(this::onUserUpdated));
        dispatcher.register(Main.SessionKicked.class, new JavaFxDispatchFunction<>(this::onYouAreKicked));
        dispatcher.register(SessionResponses.RequestImageResponse.class, new JavaFxDispatchFunction<>(this::onImageDownload));
        dispatcher.register(SessionEvents.ImageAdded.class, new JavaFxDispatchFunction<>(this::onImageAdded));
        dispatcher.register(SessionEvents.ImageTransformed.class, new JavaFxDispatchFunction<>(this::onImageTransformed));

        // błędy, do zastąpienia
        dispatcher.register(SessionResponses.InviteUserResponse.class,
                new GenericStatusDispatchFunction(this::genericError));
        dispatcher.register(SessionResponses.UploadImageResponse.class,
                new GenericStatusDispatchFunction(this::genericError));
        dispatcher.register(SessionResponses.KickUserResponse.class,
                new GenericStatusDispatchFunction(this::genericError));
        dispatcher.register(SessionResponses.ChangeUserFlagsResponse.class,
                new GenericStatusDispatchFunction(this::genericError));
        dispatcher.register(SessionResponses.ChangeSettingsResponse.class,
                new GenericStatusDispatchFunction(this::genericError));
        dispatcher.register(SessionResponses.TransformImageResponse.class,
                new GenericStatusDispatchFunction(this::genericError));

    }

    private void onImageTransformed(SessionEvents.ImageTransformed img) {
        updateImageTransform(img.getId(), new SessionImageTransformations(img.getTransformations()));
    }

    private void onImageAdded(SessionEvents.ImageAdded img) {
        connection.writeAndFlush(SessionRequests.RequestImage.newBuilder().setId(img.getId()).build());
        addImageInfo(img.getName());
    }

    private void onImageDownload(SessionResponses.RequestImageResponse img) {
        if (!img.getStatus().getSuccess())
            return;

        addImage(new SessionImage(img.getId(), img.getData(), img.getName(),
                Color.color(img.getColorR(), img.getColorG(), img.getColorB()), img.getTransformations()));
    }

    private void onYouAreKicked(Main.SessionKicked msg) {
        if (msg.getSession().getId() == id) {
            Alert al = new Alert(Alert.AlertType.WARNING);
            al.setTitle("Wyrzucony");
            al.setHeaderText("Wyrzucony");
            al.setContentText("Zostałeś wyrzucony z tej sesji");

            al.showAndWait();

            close();
        }
    }

    private void onUserUpdated(SessionEvents.UserUpdated msg) {
        updateUser(new SessionUser(msg.getUser()));
    }

    private void onParted(SessionEvents.Parted msg) {
        partUser(new SessionUser(msg.getUser()));
    }

    private void onKicked(SessionEvents.Kicked msg) {
        kickUser(new SessionUser(msg.getUser()));
    }

    private void genericError(SessionResponses.GenericResponse msg) {
        if (!msg.getSuccess())
            addMessage(null, "Błąd: " + msg.getError());
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

        EnumSet<AllowedActions> allowedActions = EnumSet.noneOf(AllowedActions.class);

        if (yourFlags.contains(SessionUserFlag.Owner))
            allowedActions = EnumSet.allOf(AllowedActions.class);

        setAllowedActions(allowedActions);

        msg.getActiveUserList()
                .forEach(a -> addActiveUser(new SessionUser(a)));

        msg.getParticipantList()
                .forEach(a -> addParticipant(new SessionUser(a)));

        msg.getImageList()
                .forEach(a -> connection.writeAndFlush(SessionRequests.RequestImage.newBuilder().setId(a).build()));
    }

    private void onJoined(SessionEvents.Joined msg) {
        joinUser(new SessionUser(msg.getUser()));
    }

    private void onNewParticipant(SessionEvents.NewParticipant msg) {
        inviteUser(new SessionUser(msg.getUser()));
    }

    private void onUserMessage(SessionEvents.UserMessage msg) {
        addMessage(new SessionUser(msg.getUser()), msg.getText());
    }

    private void onSettingsChanged(SessionEvents.SettingsChanged msg) {
        if (!msg.getData().getName().equals(data.getName())) {
            setName(msg.getData().getName());
        }

        if (!msg.getData().getState().equals(data.getState())) {
            setButtonsState(getStateButtonText(msg.getData().getState()), yourFlags.contains(SessionUserFlag.Owner));
            setState(msg.getData().getState());
        }

        if (msg.getData().getState() == Main.Session.State.Finished) {
            disableUserInput(false);
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

    @Override
    protected void onUploadImage(byte[] data, String name) {
        SessionRequests.UploadImage.Builder b = SessionRequests.UploadImage.newBuilder();

        b.setData(ByteString.copyFrom(data));
        b.setName(name);
        b.setSessionId(id);

        connection.writeAndFlush(b.build());
    }

    @Override
    protected void onUserKick(SessionUser user) {
        SessionRequests.KickUser.Builder b = SessionRequests.KickUser.newBuilder();

        b.setSessionId(id);
        b.setUserId(user.getId());

        connection.writeAndFlush(b.build());
    }

    @Override
    protected void onImageEditorSelected(Point2D start, Point2D end, double zoom) {
        if (focusedImage == null)
            return;

        SessionRequests.TransformImage.Builder b = SessionRequests.TransformImage.newBuilder();
        b.setId(focusedImage.getId());

        SessionBasic.RectFragment.Builder frag = SessionBasic.RectFragment.newBuilder();
        frag.setZoom(zoom);
        frag.setX1((int) Math.round(start.getX()));
        frag.setY1((int) Math.round(start.getY()));
        frag.setX2((int) Math.round(end.getX()));
        frag.setY2((int) Math.round(end.getY()));

        Color c = focusedImage.getColor().invert();

        frag.setColorR(c.getRed());
        frag.setColorG(c.getGreen());
        frag.setColorB(c.getBlue());

        SessionBasic.ImageTransformations.Builder b2 = SessionBasic.ImageTransformations.newBuilder();
        b2.setZoom(focusedImage.getTransformations().getZoom());
        b2.addFragments(SessionBasic.ImageFragment.newBuilder().setRect(frag).build());

        b.setTransformations(b2);

        connection.writeAndFlush(b.build());
    }

    @Override
    protected void onImageEditorZoom(double zoom) {
        if (focusedImage == null)
            return;

        SessionRequests.ZoomImage.Builder b = SessionRequests.ZoomImage.newBuilder()
                .setId(focusedImage.getId())
                .setZoom(zoom);

        connection.writeAndFlush(b.build());
    }
}
