package com.cetiti.iotp.protocol.transport.mqtt;

/**
 * @author zhouliyu
 * @since 2019-12-06 13:54:01
 */
public interface TransportServiceCallback<T> {

    void onSuccess(T msg);

    void onError(Throwable throwable);

}
