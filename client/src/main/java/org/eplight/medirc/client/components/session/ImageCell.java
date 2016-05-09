package org.eplight.medirc.client.components.session;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import org.eplight.medirc.client.data.SessionImage;

/**
 * Created by eplightning on 09.05.16.
 */
public class ImageCell extends ListCell<SessionImage> {

    private ImageView view;

    public ImageCell(ListView<SessionImage> img) {
        view = new ImageView();
        view.fitHeightProperty().bind(img.heightProperty().subtract(30));
        view.setPreserveRatio(true);
    }

    @Override
    protected void updateItem(SessionImage item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
            return;
        }

        view.setImage(item.getImg());

        setGraphic(view);
    }
}
