package org.eplight.medirc.client.instance.network.dispatcher;

import javafx.application.Platform;

public class JavaFxDispatchFunction<T> implements DispatchFunction<T> {

    private DispatchFunction<T> dispatchFunction;

    public JavaFxDispatchFunction(DispatchFunction<T> dispatchFunction) {
        this.dispatchFunction = dispatchFunction;
    }

    @Override
    public void handle(T msg) {
        Platform.runLater(() -> dispatchFunction.handle(msg));
    }
}
