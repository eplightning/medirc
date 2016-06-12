package org.eplight.medirc.client.stage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginStage extends Stage {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private ListView serverSelect;

    @FXML
    private TextField serverPort;

    @FXML
    private TextField serverAddress;

    @FXML
    private Accordion serverAccordion;

    @FXML
    private TitledPane serverListPane;

    @FXML
    private TitledPane serverCustomPane;

    @FXML
    private Button loginButton;

    private LoginFunction function;

    public LoginStage(LoginFunction func) {
        super();

        function = func;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        loader.setController(this);

        try {
            final Parent root = loader.load();
            setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTitle("Logowanie do serwera");
        setWidth(600);
        setHeight(400);

        serverAccordion.setExpandedPane(serverCustomPane);
    }

    @FXML
    protected void loginPushed() {
        // TODO: Oczywiście walidacja, wsparcie dla listy serwerów itd.
        if (serverAccordion.getExpandedPane() == serverCustomPane) {
            function.login(username.getText(), password.getText(), serverAddress.getText(), Integer.parseInt(serverPort.getText()));
        }
    }

    @FXML
    public void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loginPushed();
        }
    }


    public void setState(boolean state) {
        loginButton.setDisable(!state);
        // TODO: Ewentualnie wyłączenie całkowicie interakcji z użytkownikiem
    }

    public interface LoginFunction {
        void login(String username, String password, String serverAddress, int serverPort);
    }
}
