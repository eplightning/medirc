package org.eplight.medirc.client.components.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.eplight.medirc.client.data.Session;

import java.io.IOException;
import java.util.function.Consumer;

public class SessionCell extends ListCell<Session> {

    private Parent root;

    @FXML
    private ImageView ownerImage;

    @FXML
    private ImageView sessionImage;

    @FXML
    private Label sessionTitle;

    @FXML
    private Label usersCount;

    @FXML
    private Button acceptInvite;

    @FXML
    private Button declineInvite;

    private Session session;

    private Consumer<Session> acceptHandler;
    private Consumer<Session> declineHandler;

    public SessionCell(Consumer<Session> acceptHandler, Consumer<Session> declineHandler) {
        super();

        this.acceptHandler = acceptHandler;
        this.declineHandler = declineHandler;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Session.fxml"));
        loader.setController(this);

        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Session item, boolean empty) {
        super.updateItem(item, empty);

        this.session = item;

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        sessionTitle.setText(item.getName());
        usersCount.setText(item.getUsersCount() + " aktywnych użytkowników w sesji");
        ownerImage.setVisible(item.isOwner());

        if (item.isStarted()) {
            sessionImage.setOpacity(1.0);
        } else {
            sessionImage.setOpacity(0.6);
        }

        acceptInvite.setDisable(!item.isInvited());
        declineInvite.setDisable(!item.isInvited());
        acceptInvite.setVisible(item.isInvited());
        declineInvite.setVisible(item.isInvited());

        setGraphic(root);
    }

    @FXML
    private void onAcceptInvite(ActionEvent ev) {
        if (this.session != null)
            acceptHandler.accept(this.session);
    }

    @FXML
    private void onDeclineInvite(ActionEvent ev) {
        if (this.session != null)
            declineHandler.accept(this.session);
    }
}
