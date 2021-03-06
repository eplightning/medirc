package org.eplight.medirc.client.instance;

import javafx.scene.control.Alert;
import org.eplight.medirc.client.instance.network.Connection;
import org.eplight.medirc.client.instance.network.dispatcher.JavaFxDispatchFunction;
import org.eplight.medirc.client.instance.network.dispatcher.MessageDispatcher;
import org.eplight.medirc.client.stage.MainStage;
import org.eplight.medirc.protocol.Basic;

public class ServerInstance {

    private Connection.ErrorFunction errorFunction;
    private Connection.SuccessFunction successFunction;
    private ReturnControlFunction returnControlFunction;

    private boolean hasControl;
    private boolean shuttingDown;

    private Connection connection;
    private MessageDispatcher dispatcher;
    private Basic.HandshakeAck handshakeAck;
    private MainStage mainStage;

    public ServerInstance(ReturnControlFunction returnControlFunction, Connection.ErrorFunction error,
                          Connection.SuccessFunction success) {
        this.returnControlFunction = returnControlFunction;
        this.successFunction = success;
        this.errorFunction = error;
        dispatcher = new MessageDispatcher();

        dispatcher.register(Basic.HandshakeAck.class, new JavaFxDispatchFunction<>(this::handleHandshakeAck));
    }

    public void attemptConnection() {
        connection.connect(dispatcher, this::handleClose);
    }

    public void handleClose() {
        // jeśli jesteśmy w stanie zamykania, ignorujemy
        if (shuttingDown)
            return;

        // jeśli nie dostaliśmy jeszcze kontroli nad programem to błędy przekierowujemy
        if (!hasControl) {
            errorFunction.error("Connection has been lost");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Server connection error");
        alert.setContentText("Connection has been lost");

        alert.showAndWait();

        shutdown();
    }

    public void handleHandshakeAck(Basic.HandshakeAck msg) {
        // TODO: Może wykrywać podwójny handshakeAck i wywalić wyjątkiem
        handshakeAck = msg;

        if (msg.getSuccess()) {
            successFunction.success();
        } else {
            errorFunction.error("Authentication error: " + msg.getErrorMessage());
        }
    }

    public void setConnectionParameters(String username, String password, String address, int port) {
        connection = new Connection(errorFunction, successFunction, username, password, address, port);
    }

    public void shutdown() {
        hasControl = false;
        shuttingDown = true;

        if (connection != null) {
            connection.shutdown();
            connection = null;
        }

        if (mainStage != null) {
            mainStage.close();
            mainStage.shutdown();
            mainStage = null;
        }
    }

    public void start() {
        hasControl = true;

        mainStage = new MainStage(connection, handshakeAck, dispatcher);

        mainStage.addCloseHandlers(this::shutdown);
        mainStage.addLogoutHandlers(() -> returnControlFunction.returnControl());

        mainStage.show();
    }

    public interface ReturnControlFunction {
        void returnControl();
    }
}
