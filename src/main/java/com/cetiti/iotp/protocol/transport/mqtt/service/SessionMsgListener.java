package com.cetiti.iotp.protocol.transport.mqtt.service;

import com.cetiti.iotp.protocol.transport.mqtt.model.CommonResponsePayload;

/**
 * @author zhouliyu
 * @since 2019-12-09 14:26:58
 */
public interface SessionMsgListener {

    void onPublish(CommonResponsePayload commonResponsePayload);

}
