package com.cetiti.iotp.protocol.transport.mqtt.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Alink协议：请求
 * @author zhouliyu
 * @since 2019-11-21 15:04:09
 */
@Data
public class CommonRequestPayload<T> implements Serializable {
    private static final long serialVersionUID = -3938618402604761260L;

    /**
     * 消息ID
     * */
    private String id;

    /**
     * 协议版本号
     * */
    private String version;

    /**
     * 请求方法
     * */
    private String method;

    /**
     * 请求参数
     * */
    private T params;

}
