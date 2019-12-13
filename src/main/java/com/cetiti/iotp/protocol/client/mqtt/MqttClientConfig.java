package com.cetiti.iotp.protocol.client.mqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT Client config
 * @author zhouliyu
 * @since 2019-12-13 14:43:25
 */
@Configuration
public class MqttClientConfig {

    @Value("${client.mqtt.bind_host}")
    private String host;

    @Value("${client.mqtt.bind_port}")
    private Integer port;

    @Value("${client.mqtt.reconnect}")
    private boolean reconnect;

    @Value("${client.mqtt.reconnectDelay}")
    private long reconnectDelay;

}
