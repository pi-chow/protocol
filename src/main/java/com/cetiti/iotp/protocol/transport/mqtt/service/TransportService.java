package com.cetiti.iotp.protocol.transport.mqtt.service;
import com.cetiti.iotp.protocol.transport.mqtt.TransportServiceCallback;
import com.cetiti.iotp.protocol.transport.mqtt.model.CommonRequestPayload;
import com.cetiti.iotp.protocol.transport.mqtt.model.DeviceInfo;
import com.cetiti.iotp.protocol.transport.mqtt.model.SessionEventEnum;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * @author zhouliyu
 * @since 2019-12-06 13:53:13
 */
public interface TransportService {

    /**
     * 处理用户信息
     * */
    void process(String userName, TransportServiceCallback<DeviceInfo> callback);

    /**
     * 处理发布信息
     * */
    void process(MqttPublishMessage mqttPublishMessage, TransportServiceCallback<CommonRequestPayload> callback);

    /**
     * 处理连接
     * */
    void process(DeviceInfo deviceInfo, SessionEventEnum sessionEvent, TransportServiceCallback<Void> callback);

    /**
     * 异步注册:长连接需要手动关闭
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
