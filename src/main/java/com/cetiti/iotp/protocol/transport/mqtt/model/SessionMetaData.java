package com.cetiti.iotp.protocol.transport.mqtt.model;

import com.cetiti.iotp.protocol.transport.mqtt.service.SessionMsgListener;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * @author zhouliyu
 * @since 2019-12-10 14:18:47
 */
@Data
public class SessionMetaData implements Serializable {

    private static final long serialVersionUID = -417564343959356282L;

    private final SessionMsgListener listener;

    private final DeviceInfo deviceInfo;

    private ScheduledFuture scheduledFuture;

    private volatile long lastActivityTime;

    public SessionMetaData(DeviceInfo deviceInfo, SessionMsgListener listener) {
        this.listener = listener;
        this.deviceInfo = deviceInfo;
        this.lastActivityTime = System.currentTimeMillis();
        this.scheduledFuture = null;
    }

    public void updateLastActivityTime(){
        this.lastActivityTime = System.currentTimeMillis();
    }

    public boolean hasScheduledFuture(){
        return null != this.scheduledFuture;
    }
}
