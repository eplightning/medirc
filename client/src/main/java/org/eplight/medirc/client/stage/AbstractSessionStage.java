package org.eplight.medirc.client.stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.eplight.medirc.client.data.SessionUser;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by EpLightning on 08.05.2016.
 */
abstract public class AbstractSessionStage extends Stage {

    private Consumer<AbstractSessionStage> onCloseRun;

    @FXML
    private TextArea textInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button inviteButton;

    @FXML
    private Button sessionButton;

    @FXML
    private ListView<SessionUser> userList;

    @FXML
    private ListView<SessionUser> participantsList;

    @FXML
    private TextFlow chatView;

    @FXML
    private ScrollPane chatScroll;

    public AbstractSessionStage(Consumer<AbstractSessionStage> onCloseRun) {
        this.onCloseRun = onCloseRun;
    }

    protected void setupWindow(String sessionName) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SessionWindow.fxml"));
        loader.setController(this);

        try {
            final Parent root = loader.load();
            setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTitle("Sesja - " + sessionName);
        setWidth(1024);
        setHeight(742);

        setOnCloseRequest(this::onCloseRequest);
    }

    protected void addActiveUser(SessionUser user) {
        userList.getItems().add(user);
    }

    protected void addParticipant(SessionUser user) {
        participantsList.getItems().add(user);
    }

    protected void addMessage(SessionUser user, String msg) {
        double size = Font.getDefault().getSize();
        Text userText = null;
        Text messageText = null;

        if (user == null) {
            messageText = new Text(msg + "\n");

            messageText.setFont(Font.font("System", FontPosture.ITALIC, size));
        } else {
            userText = new Text(user.getName() + ": ");
            userText.setFont(Font.font("System", FontWeight.BOLD, size));

            messageText = new Text(msg + "\n");
        }

        if (userText != null)
            chatView.getChildren().add(userText);

        chatView.getChildren().add(messageText);

        chatView.layout();
        chatScroll.layout();
        chatScroll.setVvalue(1.0);
    }

    protected void onCloseRequest(WindowEvent event) {
        onCloseRun.accept(this);
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!textInput.getText().isEmpty())
                onSendMessage(textInput.getText());

            event.consume();
        }
    }

    @FXML
    private void onSendButtonPressed(ActionEvent event) {
        if (!textInput.getText().isEmpty())
            onSendMessage(textInput.getText());
    }

    @FXML
    private void onInviteButton(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("");

        dialog.setTitle("Zaproś użytkownika");
        dialog.setHeaderText("Zaproś użytkownika do sesji");
        dialog.setContentText("Nazwa użytkownika:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            onInviteSend(result.get());
        }
    }

    @FXML
    private void onSettingsButton(ActionEvent event) {
        // TODO:
    }

    @FXML
    private void onSessionButton(ActionEvent event) {
        onSessionAdvanced();
    }

    protected void onInviteSend(String name) {

    }

    protected void onSessionAdvanced() {

    }

    protected void setButtonsState(String stateText, boolean enabled) {
        settingsButton.setDisable(!enabled);
        sessionButton.setDisable(!enabled);
        inviteButton.setDisable(!enabled);
        sessionButton.setText(stateText);
    }

    protected void setName(String name) {
        setTitle("Sesja - " + name);
    }

    protected void disableUserInput(boolean enabled) {

    }

    protected void onSendMessage(String text) {
        textInput.setText("");
    }
}
