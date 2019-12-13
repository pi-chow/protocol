package com.cetiti.iotp.protocol.client.mqtt;

import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhouliyu
 * @since 2019-12-12 15:38:39
 */
@Component
public class MqttClientContext {

    @Getter
    @Value("${client.mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;

    @Getter
    @Setter
    private Promise<MqttConnectResult> connectFuture;

}
