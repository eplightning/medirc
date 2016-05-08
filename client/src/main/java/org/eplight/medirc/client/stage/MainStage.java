package org.eplight.medirc.client.stage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.eplight.medirc.client.ClientApplication;
import org.eplight.medirc.client.components.main.SessionCell;
import org.eplight.medirc.client.data.Session;
import org.eplight.medirc.client.data.User;
import org.eplight.medirc.client.instance.network.Connection;
import org.eplight.medirc.client.instance.network.dispatcher.JavaFxDispatchFunction;
import org.eplight.medirc.client.instance.network.dispatcher.MessageDispatcher;
import org.eplight.medirc.protocol.Basic;
import org.eplight.medirc.protocol.Main;

import java.io.IOException;
import java.util.Optional;

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

    public MainStage(Connection connection, Basic.HandshakeAck handshakeAck, MessageDispatcher dispatcher) {
        super();

        this.handshakeAck = handshakeAck;
        this.connection = connection;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        loader.setController(this);

        try {
            final Parent root = loader.load();
            setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTitle("Okno główne");
        setWidth(1024);
        setHeight(800);

        sync();

        // ODBIERANIE
        dispatcher.register(Main.SessionInvite.class, new JavaFxDispatchFunction<>(this::sessionInvite));
        dispatcher.register(Main.ActiveSessions.class, new JavaFxDispatchFunction<>(this::activeSessions));
        dispatcher.register(Main.ArchivedSessions.class, new JavaFxDispatchFunction<>(this::archivedSessions));
        dispatcher.register(Main.UserList.class, new JavaFxDispatchFunction<>(this::userList));
        dispatcher.register(Main.UserDisconnected.class, new JavaFxDispatchFunction<>(this::userDisconnected));
        dispatcher.register(Main.UserConnected.class, new JavaFxDispatchFunction<>(this::userConnected));
    }
    @FXML
    protected void newSessionPushed() {

        TextInputDialog dialog = new TextInputDialog("nowa sesja");
        dialog.setTitle("Nowa sesja");
        dialog.setHeaderText("Nowa sesja");
        dialog.setContentText("Podaj nazwę sesji:");

        Optional<String> result = dialog.showAndWait();

        Main.CreateNewSession.Builder msg = Main.CreateNewSession.newBuilder();
        if (result.isPresent()) {
            msg.setName(result.get());
        }
        connection.writeAndFlush(msg.build());
    }
    @FXML
    protected void closePushed() {
        this.connection.shutdown();
        close();
    }
    @FXML
    protected void disconnectPushed() {
        this.connection.shutdown();
    }
    public void addCloseHandlers(Runnable run) {
        setOnCloseRequest((WindowEvent ev) -> run.run());

        // TODO: Z menu Koniec
    }

    public void addLogoutHandlers(Runnable run) {
        // TODO: Z menu wyloguj
    }

    public void sync(){
        Main.SyncRequest.Builder msg = Main.SyncRequest.newBuilder();
        connection.writeAndFlush(msg.build());
    }
    public void activeSessions(Main.ActiveSessions msg){
        activeSessions.getItems().clear();
        for(Main.Session s : msg.getSessionList()) {
            activeSessions.getItems().add(new Session(s));
            activeSessions.setCellFactory(param -> new SessionCell());
        }
    }
    public void archivedSessions(Main.ArchivedSessions msg){
        archivedSessions.getItems().clear();
        for(Main.Session s : msg.getSessionList()) {
            archivedSessions.getItems().add(new Session(s));
            archivedSessions.setCellFactory(param -> new SessionCell());
        }
    }
    public void userList(Main.UserList msg){
        users.getItems().clear();
        if(!msg.getUserList().isEmpty()) {
            for (Main.User u : msg.getUserList()) {
                users.getItems().add(new User(u));
            }
        }
    }
    public void userConnected(Main.UserConnected msg){
        sync();
    }
    public void userDisconnected(Main.UserDisconnected msg){
        sync();
    }
    public void sessionInvite(Main.SessionInvite msg) {
        sync();
    }
}
