package com.cetiti.iotp.protocol.transport.mqtt.service;

import java.util.UUID;

/**
 * @author zhouliyu
 * @since 2019-12-09 14:26:58
 */
public interface SessionMsgListener {

    void onRemoteSessionCloseCommand(UUID sessionId);

}
