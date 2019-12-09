package com.cetiti.iotp.protocol.alink.payload;

import java.io.Serializable;

/**
 * Alink协议：请求
 * @author zhouliyu
 * @since 2019-11-21 15:04:09
 */
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
}
