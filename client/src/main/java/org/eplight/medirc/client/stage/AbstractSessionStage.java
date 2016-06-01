package org.eplight.medirc.client.stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.eplight.medirc.client.components.session.ImageCell;
import org.eplight.medirc.client.components.session.ImageEditor;
import org.eplight.medirc.client.data.AllowedActions;
import org.eplight.medirc.client.data.SessionImage;
import org.eplight.medirc.client.data.SessionUser;
import org.eplight.medirc.client.image.ImageFragment;
import org.eplight.medirc.protocol.Basic;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionEvents;
import org.eplight.medirc.protocol.SessionUserFlag;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by EpLightning on 08.05.2016.
 */
abstract public class AbstractSessionStage extends Stage {

    protected Consumer<AbstractSessionStage> onCloseRun;
    protected Basic.HandshakeAck handshakeAck;

    private ImageEditor imageEditor;
    protected SessionImage focusedImage;

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

    public AbstractSessionStage(Consumer<AbstractSessionStage> onCloseRun, Basic.HandshakeAck ack) {
        this.onCloseRun = onCloseRun;
        this.handshakeAck = ack;
    }

    protected void setupWindow(String sessionName, String username) {
        // wczytanie FXML-a
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SessionWindow.fxml"));
        loader.setController(this);

        try {
            final Parent root = loader.load();
            setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // właściwości okna
        setTitle("Sesja - " + sessionName + " (Użytkownik: " + username + ")");
        setWidth(1024);
        setHeight(742);
        setOnCloseRequest(this::onCloseRequest);

        // ustawianie widoku obrazka
        mainSplit.getItems().remove(imagePaneVBox);
        imageEditor = new ImageEditor();
        imagePaneScroll.setContent(imageEditor);

        imageEditor.addSelectionHandler(this::onImageEditorSelected);
        imageEditor.addZoomHandler(this::onImageEditorZoom);

        // lista obrazków
        imageList.setCellFactory(ImageCell::new);

        // menu kontekstowe
        userList.setContextMenu(new ContextMenu());
        participantsList.setContextMenu(new ContextMenu());

        MenuItem activeKick = new MenuItem("Wyrzuć");
        activeKick.setId("kick");

        MenuItem participantKick = new MenuItem("Wyrzuć");
        participantKick.setId("kick-participant");

        userList.getContextMenu().getItems().addAll(activeKick);
        participantsList.getContextMenu().getItems().addAll(participantKick);

        // akcje dla menu kontekstowych
        activeKick.setOnAction(event -> {
            SessionUser user = userList.getSelectionModel().getSelectedItem();

            if (user != null)
                onUserKick(user);
        });

        participantKick.setOnAction(event -> {
            SessionUser user = participantsList.getSelectionModel().getSelectedItem();

            if (user != null)
                onUserKick(user);
        });
    }

    protected void setAllowedActions(EnumSet<AllowedActions> actions) {
        for (MenuItem i : userList.getContextMenu().getItems()) {
            switch (i.getId()) {
                case "kick":
                    i.setDisable(!actions.contains(AllowedActions.Kick));
                    break;
            }
        }

        for (MenuItem i : participantsList.getContextMenu().getItems()) {
            switch (i.getId()) {
                case "kick-participant":
                    i.setDisable(!actions.contains(AllowedActions.Kick));
                    break;
            }
        }
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

        SessionUser oldUser = null;

        if (index != -1) {
            oldUser = userList.getItems().get(index);
            userList.getItems().set(index, user);
        }

        index = participantsList.getItems().indexOf(user);

        if (index != -1) {
            oldUser = participantsList.getItems().get(index);
            participantsList.getItems().set(index, user);
        }

        if (oldUser != null) {
            if (oldUser.getFlags().contains(SessionUserFlag.Invited)
                    && !user.getFlags().contains(SessionUserFlag.Invited)) {
                addMessage(null, user.getName() + " zaakceptował zaproszenie");
            }

            if (oldUser.getFlags().contains(SessionUserFlag.Voice)
                    && !user.getFlags().contains(SessionUserFlag.Voice)) {
                addMessage(null, user.getName() + " stracił prawo głosu");
            } else if (!oldUser.getFlags().contains(SessionUserFlag.Voice)
                    && user.getFlags().contains(SessionUserFlag.Voice)) {
                addMessage(null, user.getName() + " otrzymał prawo głosu");
            }
        }
    }

    protected void updateImageTransform(int id, double zoom) {
        SessionImage img = null;

        for (SessionImage i : imageList.getItems()) {
            if (i.getId() == id) {
                img = i;
                break;
            }
        }

        if (img == null)
            return;

        img.setZoom(zoom);

        if (img == focusedImage) {
            imageEditor.getFragments().clear();
            imageEditor.getFragments().addAll(img.getFragments());
            imageEditor.changeZoom(img.getZoom());
            imageEditor.clearSelection();
        }
    }

    protected void updateImageFragments(int id, List<ImageFragment> fragments) {
        SessionImage img = null;

        for (SessionImage i : imageList.getItems()) {
            if (i.getId() == id) {
                img = i;
                break;
            }
        }

        if (img == null)
            return;

        img.setFragments(fragments);

        if (img == focusedImage) {
            imageEditor.getFragments().clear();
            imageEditor.getFragments().addAll(img.getFragments());
            imageEditor.changeZoom(img.getZoom());
            imageEditor.clearSelection();
        }
    }

    protected void addParticipant(SessionUser user) {
        participantsList.getItems().add(user);
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

    protected void inviteUser(SessionUser user) {
        addParticipant(user);
        addMessage(null, user.getName() + " został zaproszony do sesji");
    }

    protected void kickUser(SessionUser user, SessionEvents.Kicked.Reason reason) {
        participantsList.getItems().remove(user);

        switch (reason) {
            case Declined:
                addMessage(null, user.getName() + " odrzucił zaproszenie do sesji");
                break;

            default:
                addMessage(null, user.getName() + " został wyrzucony z sesji");
        }
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

    protected void addImage(SessionImage img) {
        imageList.getItems().add(img);
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
            imageEditor.changeZoom(img.getZoom());
            imageEditor.getFragments().clear();
            imageEditor.getFragments().addAll(img.getFragments());
        }
    }

    @FXML
    private void onSessionButton(ActionEvent event) {
        onSessionAdvanced();
    }

    protected void onImageEditorSelected(Point2D start, Point2D end, double zoom) {
    }

    protected void onImageEditorZoom(double zoom) {
    }

    protected void onCloseRequest(WindowEvent event) {
        onCloseRun.accept(this);
    }

    protected void onUploadImage(byte[] data, String name) {

    }

    protected void onInviteSend(String name) {

    }

    protected void onSessionAdvanced() {

    }

    protected void onSendMessage(String text) {
        textInput.setText("");
    }

    protected void onUserKick(SessionUser user) {

    }
}
