package org.nustaq.kontraktor.barebone;

/**
 * Created by ruedi on 05/06/15.
 */
public interface BBCallback<T> {

    void receive(T result, Object error);
}
