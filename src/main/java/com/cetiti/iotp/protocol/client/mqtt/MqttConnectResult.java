package com.cetiti.iotp.protocol.client.mqtt;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import lombok.Getter;

/**
 * @author zhouliyu
 * @since 2019-12-12 15:26:01
 */
public class MqttConnectResult {

    private final boolean success;

    @Getter
    private final MqttConnectReturnCode returnCode;

    @Getter
    private final ChannelFuture closeFuture;

    public MqttConnectResult(boolean success, MqttConnectReturnCode returnCode, ChannelFuture closeFuture) {
        this.success = success;
        this.returnCode = returnCode;
        this.closeFuture = closeFuture;
    }

    public boolean isConnect(){
        return success;
    }

}
