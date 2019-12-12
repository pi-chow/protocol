package com.cetiti.iotp.protocol.transport.mqtt.service;
import com.cetiti.iotp.protocol.transport.mqtt.TransportServiceCallback;
import com.cetiti.iotp.protocol.transport.mqtt.model.CommonRequestPayload;
import com.cetiti.iotp.protocol.transport.mqtt.model.DeviceInfo;
import com.cetiti.iotp.protocol.transport.mqtt.enums.SessionEventEnum;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * 传输层服务
 * @author zhouliyu
 * @since 2019-12-06 13:53:13
 */
public interface TransportService<T> {

    /**
     * 处理用户信息
     * */
    void process(String userName, TransportServiceCallback<DeviceInfo> callback);

    /**
     * 处理发布信息
     * */
    void process(DeviceInfo deviceInfo, T msg, TransportServiceCallback<CommonRequestPayload> callback);

    /**
     * 处理连接
     * */
    void process(DeviceInfo deviceInfo, SessionEventEnum sessionEvent, TransportServiceCallback<Void> callback);

    /**
     * 异步注册:长连接, 需要手动关闭
     * */
    void registerAsyncSession(DeviceInfo deviceInfo, SessionMsgListener listener);

    /**
     * 同步注册：短连接，超时内保持会话
     * */
    void registerSyncSession(DeviceInfo deviceInfo, SessionMsgListener listener, long timeout);

    /**
     * 上报心跳
     * */
    void reportActivity(DeviceInfo deviceInfo);

    /**
     * 取消注册
     * */
    void deregisterSession(DeviceInfo deviceInfo);


}
