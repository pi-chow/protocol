package com.cetiti.iotp.protocol.transport.mqtt.enums;

/**
 * @author zhouliyu
 * @since 2019-12-10 18:26:42
 */
public enum SessionEventEnum {

    /**
     * 会话开启
     * */
    OPEN(0),
    /**
     * 会话关闭
     * */
    CLOSED(1),
    /**
     * 未识别会话
     * */
    UNRECOGNIZED(-1);

    private Integer code;

    SessionEventEnum(int code) {
        this.code = code;
    }
}
