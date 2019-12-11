package com.cetiti.iotp.protocol.transport.mqtt;

import com.cetiti.iotp.protocol.transport.mqtt.service.TransportService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhouliyu
 * @since 2019-12-06 15:19:37
 */
@Component
public class MqttTransportContext {

    @Getter
    @Autowired
    private TransportService transportService;


    @Getter
    @Value("${transport.mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;
}
