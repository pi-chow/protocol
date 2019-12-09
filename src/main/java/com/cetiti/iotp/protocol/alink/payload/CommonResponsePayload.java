package com.cetiti.iotp.protocol.alink.payload;

import java.io.Serializable;

/**
 * Alink协议：响应
 * @author zhouliyu
 * @since 2019-11-21 15:12:30
 */
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
    private String msg;

    /**
     * 请求成功返回的数据
     * */
    private T data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess(){

        return this.code >= 200 && this.code < 300;

    }
}
