package com.cetiti.iotp.protocol.client.mqtt;

/**
 * @author zhouliyu
 * @since 2019-12-12 17:11:13
 */
public interface MqttClientCallback {

    void connectionLost(Throwable cause);
}
