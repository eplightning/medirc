package org.eplight.medirc.client.stage;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TimelineStage extends Stage {

    @FXML
    private Slider slider;

    private Runnable play;

    public TimelineStage(Stage parent, int seconds, Runnable play) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Timeline.fxml"));
        loader.setController(this);

        this.play = play;

        try {
            final Parent root = loader.load();
            setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setTitle("Linia czasu sesji");
        slider.setMax(seconds);

        initOwner(parent);
    }

    public DoubleProperty valueProperty() {
        return slider.valueProperty();
    }

    @FXML
    private void onPlayPauseClicked(ActionEvent event) {
        play.run();
    }
}
