package com.cetiti.iotp.protocol.transport.mqtt.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouliyu
 * @since 2019-12-09 15:54:07
 */
@Data
public class DevicePublishMsg implements Serializable{
    private static final long serialVersionUID = 2299489107855757822L;

    private String topicName;

    private byte[] payload;
}
