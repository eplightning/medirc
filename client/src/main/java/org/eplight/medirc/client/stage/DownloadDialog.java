package org.eplight.medirc.client.stage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class DownloadDialog extends Stage {

    @FXML
    private ProgressBar progress;

    @FXML
    private Label downloadedBlocks;

    @FXML
    private Label remainingBlocks;

    @FXML
    private Label blockSize;

    @FXML
    private Label filenameLabel;

    private int blocksTotal;

    private int blocksDownloaded;

    private String filename;

    public DownloadDialog(int size, int blocksTotal, String filename) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DownloadDialog.fxml"));
        loader.setController(this);

        try {
            final Parent root = loader.load();
            setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.blocksTotal = blocksTotal;
        this.blocksDownloaded = 0;
        this.filename = filename;

        setTitle("Pobieranie sesji");
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);


        filenameLabel.setText("Nazwa pliku: " + filename);
        progress.setProgress(0);
        downloadedBlocks.setText("Pobranych bloków: 0");
        remainingBlocks.setText("Pozostało bloków: " + blocksTotal);
        blockSize.setText("Rozmiar bloku: " + size + " bajtów");
    }

    public void blockReceived() {
        if (blocksDownloaded < blocksTotal)
            blocksDownloaded++;

        downloadedBlocks.setText("Pobranych bloków: " + blocksDownloaded);
        remainingBlocks.setText("Pozostało bloków: " + (blocksTotal - blocksDownloaded));
        progress.setProgress((double) blocksDownloaded / (double) blocksTotal);
    }

    public String getFilename() {
        return filename;
    }
}
