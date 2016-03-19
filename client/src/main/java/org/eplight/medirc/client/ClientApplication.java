package org.eplight.medirc.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    static public void main(String[] argv)
    {
        launch(argv);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.show();
    }
}
