package org.eplight.medirc.client.stage;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.eplight.medirc.client.components.main.SessionCell;
import org.eplight.medirc.client.data.Session;
import org.eplight.medirc.client.data.User;
import org.eplight.medirc.client.instance.network.Connection;
import org.eplight.medirc.client.instance.network.dispatcher.JavaFxDispatchFunction;
import org.eplight.medirc.client.instance.network.dispatcher.MessageDispatcher;
import org.eplight.medirc.protocol.Basic;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionRequests;
import org.eplight.medirc.protocol.SessionResponses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MainStage extends Stage {

    private Basic.HandshakeAck handshakeAck;
    private Connection connection;
    private MessageDispatcher dispatcher;

    @FXML
    private ListView<Session> activeSessions;

    @FXML
    private ListView<Session> archivedSessions;

    @FXML
    private ListView<User> users;

    private Runnable closeHandler;
    private Runnable logoutHandler;

    private List<AbstractSessionStage> sessionStages = new ArrayList<>();

    public MainStage(Connection connection, Basic.HandshakeAck handshakeAck, MessageDispatcher dispatcher) {
        super();

        this.handshakeAck = handshakeAck;
        this.connection = connection;
        this.dispatcher = dispatcher;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        loader.setController(this);

        try {
            final Parent root = loader.load();
            setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTitle("Okno główne - medirc (Użytkownik: " + handshakeAck.getName() + ")");
        setWidth(1024);
        setHeight(800);

        activeSessions.setCellFactory(param -> new SessionCell(this::onAcceptInvite, this::onDeclineInvite));
        archivedSessions.setCellFactory(param -> new SessionCell(this::onAcceptInvite, this::onDeclineInvite));

        activeSessions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Session s = activeSessions.getSelectionModel().getSelectedItem();

                    if (s != null) {
                        joinSession(s);
                    }
                }
            }
        });

        dispatcher.register(Main.SessionInvite.class, new JavaFxDispatchFunction<>(this::sessionInvite));
        dispatcher.register(Main.ActiveSessions.class, new JavaFxDispatchFunction<>(this::activeSessions));
        dispatcher.register(Main.ArchivedSessions.class, new JavaFxDispatchFunction<>(this::archivedSessions));
        dispatcher.register(Main.UserList.class, new JavaFxDispatchFunction<>(this::userList));
        dispatcher.register(Main.UserDisconnected.class, new JavaFxDispatchFunction<>(this::userDisconnected));
        dispatcher.register(Main.UserConnected.class, new JavaFxDispatchFunction<>(this::userConnected));
        dispatcher.register(Main.SessionUpdated.class, new JavaFxDispatchFunction<>(this::sessionUpdated));
        dispatcher.register(Main.SessionKicked.class, new JavaFxDispatchFunction<>(this::sessionKicked));
        dispatcher.register(SessionResponses.JoinResponse.class, new JavaFxDispatchFunction<>(this::onJoinResponse));

        connection.writeAndFlush(Main.SyncRequest.newBuilder().build());
    }

    private void onAcceptInvite(Session session) {
        if (session.isInvited())
            connection.writeAndFlush(SessionRequests.AcceptInviteRequest.newBuilder().setId(session.getId()).build());
    }

    private void onDeclineInvite(Session session) {
        if (session.isInvited())
            connection.writeAndFlush(SessionRequests.DeclineInviteRequest.newBuilder().setId(session.getId()).build());
    }

    @FXML
    protected void newSessionPushed() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nowa sesja");
        dialog.setHeaderText("Nowa sesja");
        dialog.setContentText("Podaj nazwę sesji:");

        Optional<String> result = dialog.showAndWait();

        Main.CreateNewSession.Builder msg = Main.CreateNewSession.newBuilder();

        if (result.isPresent()) {
            msg.setName(result.get());

            connection.writeAndFlush(msg.build());
        }
    }

    @FXML
    protected void closePushed() {
        closeHandler.run();
    }

    @FXML
    protected void disconnectPushed() {
        logoutHandler.run();
    }

    public void addCloseHandlers(Runnable run) {
        setOnCloseRequest((WindowEvent ev) -> run.run());

        closeHandler = run;
    }

    public void addLogoutHandlers(Runnable run) {
        logoutHandler = run;
    }

    private void activeSessions(Main.ActiveSessions msg){
        activeSessions.getItems().clear();

        for (Main.Session s : msg.getSessionList()) {
            activeSessions.getItems().add(new Session(s));
        }
    }

    private void archivedSessions(Main.ArchivedSessions msg){
        archivedSessions.getItems().clear();

        for (Main.Session s : msg.getSessionList()) {
            archivedSessions.getItems().add(new Session(s));
        }
    }

    private void userList(Main.UserList msg){
        users.getItems().clear();

        if (!msg.getUserList().isEmpty()) {
            for (Main.User u : msg.getUserList()) {
                users.getItems().add(new User(u));
            }
        }
    }

    private void userConnected(Main.UserConnected msg){
        users.getItems().add(new User(msg.getUser()));
    }

    private void userDisconnected(Main.UserDisconnected msg){
        users.getItems().remove(new User(msg.getUser()));
    }

    private void sessionInvite(Main.SessionInvite msg) {
        activeSessions.getItems().add(new Session(msg.getSession()));
    }

    private void sessionKicked(Main.SessionKicked msg) {
        activeSessions.getItems().remove(new Session(msg.getSession()));
    }

    private void sessionUpdated(Main.SessionUpdated msg) {
        Session s = new Session(msg.getSession());

        int index = activeSessions.getItems().indexOf(s);

        if (index == -1) {
            if (s.isArchived()) {
                archivedSessions.getItems().add(s);
            } else {
                activeSessions.getItems().add(s);
            }
        } else {
            if (s.isArchived()) {
                activeSessions.getItems().remove(index);
                archivedSessions.getItems().add(s);
            } else {
                activeSessions.getItems().set(index, s);
            }
        }
    }

    private void joinSession(Session session) {
        SessionRequests.JoinRequest.Builder msg = SessionRequests.JoinRequest.newBuilder()
                .setId(session.getId());

        connection.writeAndFlush(msg.build());
    }

    private void onJoinResponse(SessionResponses.JoinResponse msg) {
        if (!msg.getStatus().getSuccess()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd dołączania do sesji");
            alert.setContentText(msg.getStatus().getError());

            alert.showAndWait();

            return;
        }

        sessionStages.add(new ActiveSessionStage(msg, connection, dispatcher, this::onCloseSession, handshakeAck));
    }

    private void onCloseSession(AbstractSessionStage s) {
        sessionStages.remove(s);
    }

    public void shutdown() {
        sessionStages.forEach(Stage::close);
    }
}
