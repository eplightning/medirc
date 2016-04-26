package org.eplight.medirc.client.instance.network.dispatcher;

public interface DispatchFunction<T> {
    void handle(T msg);
}
