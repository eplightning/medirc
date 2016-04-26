package org.eplight.medirc.client;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.eplight.medirc.client.instance.ServerInstance;
import org.eplight.medirc.client.stage.LoginStage;

public class ClientApplication extends Application {

    /**
     * Blokuje wszystkie żądania okna logowania
     */
    private boolean attemptInProgress;

    /**
     * Blokuje dodatkowe błędy
     */
    private boolean errorHandled;

    /**
     * Okno logowania
     */
    private LoginStage login;

    /**
     * Instancja połączenia z serwerem
     */
    private ServerInstance instance;

    /**
     * Startuje program
     *
     * @param argv
     */
    static public void main(String[] argv)
    {
        launch(argv);
    }

    /**
     * Wywoływane przez JavaFX
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        startConnectionPrompt();
    }

    /**
     * Obsługa żądania logowania
     *
     * @param username Login
     * @param password Hasło
     * @param address Adres serwera
     * @param port Port
     */
    public void handleLogin(String username, String password, String address, int port) {
        if (attemptInProgress)
            return;

        attemptInProgress = true;
        login.setState(false);

        // próba połączenia
        instance = new ServerInstance(this::startConnectionPrompt, this::handleError, this::handleSuccess);
        instance.setConnectionParameters(username, password, address, port);
        instance.attemptConnection();
    }

    /**
     * Nie udało się połączyć
     *
     * @param error Szczegóły błędu
     */
    public void handleError(String error) {
        if (errorHandled || !attemptInProgress)
            return;

        errorHandled = true;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Błąd łączenia z serwerem");
        alert.setContentText(error);

        alert.showAndWait();

        startConnectionPrompt();
    }

    /**
     * Udało się połączyć, przekazujemy kontrolę nad programem do instancji połączenia
     */
    public void handleSuccess() {
        if (!attemptInProgress || errorHandled)
            return;

        login.close();
        login = null;

        instance.start();
    }

    /**
     * Przejmujemy kontrole nad programem, wyświetlamy okienko logowania
     */
    public void startConnectionPrompt() {
        if (instance != null) {
            instance.shutdown();
            instance = null;
        }

        attemptInProgress = false;
        errorHandled = false;

        if (login == null) {
            login = new LoginStage(this::handleLogin);
            login.show();
        } else {
            login.setState(true);
        }
    }
}
