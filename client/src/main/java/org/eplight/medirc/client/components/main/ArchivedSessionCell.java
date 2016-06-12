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

public class ArchivedSessionCell extends ListCell<Session> {

    private Parent root;

    @FXML
    private Label sessionTitle;

    private Session session;

    private Consumer<Session> downloadHandler;

    public ArchivedSessionCell(Consumer<Session> downloadHandler) {
        super();

        this.downloadHandler = downloadHandler;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SessionArchived.fxml"));
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

        setGraphic(root);
    }

    @FXML
    private void onDownload(ActionEvent ev) {
        if (this.session != null)
            downloadHandler.accept(this.session);
    }
}
