package org.eplight.medirc.client.stage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.eplight.medirc.client.components.main.SessionCell;
import org.eplight.medirc.client.data.Session;
import org.eplight.medirc.client.instance.network.Connection;
import org.eplight.medirc.client.instance.network.dispatcher.MessageDispatcher;
import org.eplight.medirc.protocol.Basic;
import org.eplight.medirc.protocol.Main;

import java.io.IOException;

public class MainStage extends Stage {

    private Basic.HandshakeAck handshakeAck;
    private Connection connection;
    private MessageDispatcher dispatcher;

    @FXML
    private ListView<Session> activeSessions;

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

        Main.Session test = Main.Session.newBuilder().setName("Test").build();

        activeSessions.getItems().add(new Session(test));
        activeSessions.setCellFactory(param -> new SessionCell());

        // WYSYŁANIE
        /*Main.GetActiveSessionList.Builder msg = Main.GetActiveSessionList.newBuilder();
        msg.setTest("test");
        connection.writeAndFlush(msg.build());

        // ODBIERANIE
        dispatcher.register(Main.ActiveSessions.class, new JavaFxDispatchFunction<>(this::asdsadads));*/
    }

    public void addCloseHandlers(Runnable run) {
        setOnCloseRequest((WindowEvent ev) -> run.run());

        // TODO: Z menu Koniec
    }

    public void addLogoutHandlers(Runnable run) {
        // TODO: Z menu wyloguj
    }

    public void asdsadads(Main.ActiveSessions msg) {

    }
}
