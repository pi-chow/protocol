package com.cetiti.iotp.protocol.transport.mqtt.model;

/**
 * @author zhouliyu
 * @since 2019-12-10 18:26:42
 */
public enum SessionEventEnum {

    OPEN(0),
    CLOSED(1),
    UNRECOGNIZED(-1);

    private Integer code;

    SessionEventEnum(int code) {
        this.code = code;
    }
}
