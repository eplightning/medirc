package org.eplight.medirc.client.stage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.eplight.medirc.client.components.session.ImageCell;
import org.eplight.medirc.client.components.session.ImageEditor;
import org.eplight.medirc.client.data.SessionImage;
import org.eplight.medirc.client.data.SessionUser;
import org.eplight.medirc.protocol.Basic;
import org.eplight.medirc.protocol.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by EpLightning on 08.05.2016.
 */
abstract public class AbstractSessionStage extends Stage {

    protected Consumer<AbstractSessionStage> onCloseRun;
    protected Basic.HandshakeAck handshakeAck;

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

    @FXML
    private ListView<SessionImage> imageList;

    @FXML
    private SplitPane mainSplit;

    @FXML
    private VBox imagePaneVBox;

    @FXML
    private ScrollPane imagePaneScroll;

    private ImageEditor imageEditor;

    private ContextMenu activeUserContext;
    private ContextMenu participantContext;

    private SessionImage focusedImage;

    public AbstractSessionStage(Consumer<AbstractSessionStage> onCloseRun, Basic.HandshakeAck ack) {
        this.onCloseRun = onCloseRun;
        this.handshakeAck = ack;
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

        setTitle("Sesja - " + sessionName + " (Użytkownik: " + handshakeAck.getName() + ")");
        setWidth(1024);
        setHeight(742);

        mainSplit.getItems().remove(imagePaneVBox);
        imageEditor = new ImageEditor();

        imagePaneScroll.setContent(imageEditor);
        imagePaneScroll.setPannable(true);

        imageList.setCellFactory(sessionImageListView -> new ImageCell(sessionImageListView));

        setOnCloseRequest(this::onCloseRequest);

        activeUserContext = new ContextMenu();

        MenuItem kick = new MenuItem("Wyrzuć");
        activeUserContext.getItems().add(kick);

        participantContext = new ContextMenu();
        MenuItem kick2 = new MenuItem("Wyrzuć");
        participantContext.getItems().add(kick2);

        kick.setOnAction(event -> {
            SessionUser user = userList.getSelectionModel().getSelectedItem();

            if (user != null)
                onUserKick(user);
        });

        kick2.setOnAction(event -> {
            SessionUser user = participantsList.getSelectionModel().getSelectedItem();

            if (user != null)
                onUserKick(user);
        });
    }

    protected void enableContextMenu(boolean enabled) {
        if (enabled) {
            userList.setContextMenu(activeUserContext);
            participantsList.setContextMenu(participantContext);
        } else {
            userList.setContextMenu(null);
            participantsList.setContextMenu(null);
        }
    }

    protected void onUserKick(SessionUser user) {

    }

    protected void addActiveUser(SessionUser user) {
        userList.getItems().add(user);
    }

    protected void partUser(SessionUser user) {
        userList.getItems().remove(user);
        addMessage(null, user.getName() + " opuścił sesję");
    }

    protected void joinUser(SessionUser user) {
        addActiveUser(user);
        addMessage(null, user.getName() + " dołączył do sesji");
    }

    protected void updateUser(SessionUser user) {
        int index = userList.getItems().indexOf(user);

        if (index != -1) {
            userList.getItems().set(index, user);
        }

        index = participantsList.getItems().indexOf(user);

        if (index != -1) {
            participantsList.getItems().set(index, user);
        }

        addMessage(null, user.getName() + " został zaaktualizowany");
    }

    protected void addParticipant(SessionUser user) {
        participantsList.getItems().add(user);
    }

    protected void inviteUser(SessionUser user) {
        addParticipant(user);
        addMessage(null, user.getName() + " został zaproszony do sesji");
    }

    protected void kickUser(SessionUser user) {
        participantsList.getItems().remove(user);
        addMessage(null, user.getName() + " został wyrzucony z sesji");
    }

    protected void addImageInfo(String info) {
        addMessage(null, "Zdjęcie " + info + " zostało dodane");
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
        FileChooser chooser = new FileChooser();

        File file = chooser.showOpenDialog(this);

        if (file != null) {
            byte[] data;

            try {
                BufferedImage img = ImageIO.read(file);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                ImageIO.write(img, "png", stream);

                data = stream.toByteArray();
                onUploadImage(data, file.getName());
            } catch (IOException e) {
                addMessage(null, "Nieprawidłowy format obrazka: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onImageClicked(MouseEvent event) {
        SessionImage img = imageList.getSelectionModel().getSelectedItem();

        // odznaczanie obrazka
        if (focusedImage == img) {
            imageList.getSelectionModel().clearSelection();
            img = null;
        }

        if (img == null) {
            if (focusedImage != null) {
                mainSplit.getItems().remove(imagePaneVBox);
                focusedImage = null;
            }
        } else {
            if (focusedImage == null) {
                mainSplit.getItems().add(imagePaneVBox);
            }

            focusedImage = img;

            imageEditor.setImage(img.getImg(), img.getColor());

        }
    }

    protected void addImage(SessionImage img) {
        imageList.getItems().add(img);
    }

    protected void onUploadImage(byte[] data, String name) {

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
        setTitle("Sesja - " + name + " (Użytkownik: " + handshakeAck.getName() + ")");
        addMessage(null, "Nazwa sesji została zmieniona");
    }

    protected void setState(Main.Session.State state) {
        if (state == Main.Session.State.Finished) {
            addMessage(null, "Sesja została zakończona");
        } else if (state == Main.Session.State.Started) {
            addMessage(null, "Sesja została rozpoczęta");
        }
    }

    protected void disableUserInput(boolean enabled) {
        textInput.setDisable(!enabled);
        settingsButton.setDisable(!enabled);
        sendButton.setDisable(!enabled);
        inviteButton.setDisable(!enabled);
        sessionButton.setDisable(!enabled);
    }

    protected void onSendMessage(String text) {
        textInput.setText("");
    }
}
