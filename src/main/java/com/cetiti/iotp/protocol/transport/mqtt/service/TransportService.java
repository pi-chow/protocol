package com.cetiti.iotp.protocol.transport.mqtt.service;
import com.cetiti.iotp.protocol.transport.mqtt.TransportServiceCallback;
import com.cetiti.iotp.protocol.transport.mqtt.model.CommonRequestPayload;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * @author zhouliyu
 * @since 2019-12-06 13:53:13
 */
public interface TransportService {

    void process(String userName, TransportServiceCallback<String> callback);

    void process(MqttPublishMessage mqttPublishMessage, TransportServiceCallback<CommonRequestPayload> callback);

}
