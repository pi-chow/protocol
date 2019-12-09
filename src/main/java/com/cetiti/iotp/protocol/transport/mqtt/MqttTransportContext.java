package com.cetiti.iotp.protocol.transport.mqtt;

import com.cetiti.iotp.protocol.transport.mqtt.service.TransportService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhouliyu
 * @since 2019-12-06 15:19:37
 */
@Component
public class MqttTransportContext {

    @Getter
    @Autowired(required = false)
    private TransportService transportService;

}
