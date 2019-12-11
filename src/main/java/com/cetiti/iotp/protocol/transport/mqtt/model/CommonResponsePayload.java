package com.cetiti.iotp.protocol.transport.mqtt.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Alink协议：响应
 * @author zhouliyu
 * @since 2019-11-21 15:12:30
 */
@Data
public class CommonResponsePayload<T> implements Serializable {
    private static final long serialVersionUID = -3000276318160751902L;

    /**
     * 消息ID
     * */
    private String id;

    /**
     * 结果状态码
     * */
    private Integer code;

    /**
     * 结果描述
     * */
    private String message;

    /**
     * 请求成功返回的数据
     * */
    private T data;

    /**
     * 方法名
     * */
    private String method;

    /**
     * 版本号
     * */
    private String version = "1.0";
}
