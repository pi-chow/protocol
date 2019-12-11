package com.cetiti.iotp.protocol.transport.mqtt.service;

import com.cetiti.iotp.protocol.transport.mqtt.TransportServiceCallback;
import com.cetiti.iotp.protocol.transport.mqtt.model.CommonRequestPayload;
import com.cetiti.iotp.protocol.transport.mqtt.model.DeviceInfo;
import com.cetiti.iotp.protocol.transport.mqtt.model.SessionEventEnum;
import com.cetiti.iotp.protocol.transport.mqtt.model.SessionMetaData;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author zhouliyu
 * @since 2019-12-06 17:01:41
 */
@Slf4j
public abstract class AbstractTransportService implements TransportService {

    /**
     * 回调函数线程
     * */
    protected ExecutorService transportCallbackExecutor;

    protected ScheduledExecutorService schedulerExecutor;

    private ConcurrentMap<UUID, SessionMetaData> sessions = new ConcurrentHashMap<>();


    public void init(){

        transportCallbackExecutor = Executors.newWorkStealingPool(10);

        schedulerExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void process(DeviceInfo deviceInfo, SessionEventEnum sessionEvent, TransportServiceCallback<Void> callback) {
        reportActivityInternal(deviceInfo);
        doProcess(deviceInfo, sessionEvent, callback);
    }

    @Override
    public void process(DeviceInfo deviceInfo, MqttPublishMessage msg, TransportServiceCallback<CommonRequestPayload> callback) {
        reportActivityInternal(deviceInfo);
        doProcess(deviceInfo, msg, callback);
    }

    protected abstract void doProcess(DeviceInfo deviceInfo, SessionEventEnum sessionEvent, TransportServiceCallback<Void> callback);

    protected abstract void doProcess(DeviceInfo deviceInfo, MqttPublishMessage msg, TransportServiceCallback<CommonRequestPayload> callback);

    @Override
    public void registerAsyncSession(DeviceInfo deviceInfo, SessionMsgListener listener) {

        sessions.putIfAbsent(deviceInfo.getSessionId(), new SessionMetaData(deviceInfo, listener));

    }

    @Override
    public void registerSyncSession(DeviceInfo deviceInfo, SessionMsgListener listener, long timeout) {
        SessionMetaData currentSession = new SessionMetaData(deviceInfo, listener);
        sessions.putIfAbsent(deviceInfo.getSessionId(), currentSession);

        //保持会话时长
        ScheduledFuture scheduledFuture = schedulerExecutor.schedule(
                () -> {
                    listener.onRemoteSessionCloseCommand(deviceInfo.getSessionId());
                    deregisterSession(deviceInfo);
                }, timeout, TimeUnit.MILLISECONDS
        );

        currentSession.setScheduledFuture(scheduledFuture);
    }

    @Override
    public void deregisterSession(DeviceInfo deviceInfo) {

        SessionMetaData currentSession = sessions.get(deviceInfo.getSessionId());
        if (currentSession.hasScheduledFuture()) {
            log.debug("Stopping scheduler to avoid resending response if request has been ack.");
            currentSession.getScheduledFuture().cancel(false);
        }
        sessions.remove(deviceInfo.getSessionId());

    }

    @Override
    public void reportActivity(DeviceInfo deviceInfo) {

        reportActivityInternal(deviceInfo);
    }

    private SessionMetaData reportActivityInternal(DeviceInfo deviceInfo) {

        UUID sessionId = deviceInfo.getSessionId();
        SessionMetaData currentSession = sessions.get(sessionId);
        if (currentSession != null) {
            currentSession.updateLastActivityTime();
        }
        return currentSession;
    }

    public void destory(){

        if(schedulerExecutor != null) {
            schedulerExecutor.shutdownNow();
        }

        if (transportCallbackExecutor != null) {
            transportCallbackExecutor.shutdownNow();
        }
    }
}
