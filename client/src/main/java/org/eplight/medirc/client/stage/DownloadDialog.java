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

        setTitle("Downloading session");
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);


        filenameLabel.setText("Filename: " + filename);
        progress.setProgress(0);
        downloadedBlocks.setText("Downloaded blocks: 0");
        remainingBlocks.setText("Blocks remaining: " + blocksTotal);
        blockSize.setText("Block size: " + size + " bytes");
    }

    public void blockReceived() {
        if (blocksDownloaded < blocksTotal)
            blocksDownloaded++;

        downloadedBlocks.setText("Downloaded blocks: " + blocksDownloaded);
        remainingBlocks.setText("Blocks remaining: " + (blocksTotal - blocksDownloaded));
        progress.setProgress((double) blocksDownloaded / (double) blocksTotal);
    }

    public String getFilename() {
        return filename;
    }
}
