package org.eplight.medirc.client.components.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.eplight.medirc.client.data.Session;

import java.io.IOException;

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

    public SessionCell() {
        super();

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

        setGraphic(root);
    }
}
