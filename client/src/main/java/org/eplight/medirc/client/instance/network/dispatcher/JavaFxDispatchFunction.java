package org.eplight.medirc.client.instance.network.dispatcher;

public class JavaFxDispatchFunction<T> implements DispatchFunction<T> {

    private DispatchFunction<T> dispatchFunction;

    public JavaFxDispatchFunction(DispatchFunction<T> dispatchFunction) {
        this.dispatchFunction = dispatchFunction;
    }

    @Override
    public void handle(T msg) {
        // TODO: Do wywalenia cała klasa teraz ..
        dispatchFunction.handle(msg);
        //Platform.runLater(() -> dispatchFunction.handle(msg));
    }
}
