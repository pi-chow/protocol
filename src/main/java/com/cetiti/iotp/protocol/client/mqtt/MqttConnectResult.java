package com.cetiti.iotp.protocol.client.mqtt;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zhouliyu
 * @since 2019-12-12 15:26:01
 */
@ToString
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
