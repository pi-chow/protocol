package com.cetiti.iotp.protocol.client.mqtt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhouliyu
 * @since 2019-12-12 17:29:51
 */
@Slf4j
public class MqttClientHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private Promise<MqttConnectResult> connectFuture;

    MqttClientHandler(Promise<MqttConnectResult> connectFuture) {
        this.connectFuture = connectFuture;
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {

        switch (msg.fixedHeader().messageType()) {

            case CONNACK:
                log.info("received connect ack msg : {}", msg.toString());
                break;
            case PUBACK:
                break;
            case PUBLISH:
                log.info("received publish msg: {}", msg.toString());
                break;
            case SUBACK:
                break;
            case UNSUBACK:
                break;
            case PUBREC:
                break;
            case PUBREL:
                break;
            case PUBCOMP:
                break;
            default:
                break;
        }


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.writeAndFlush(createMqttConnectMsg());

    }

    private MqttConnectMessage createMqttConnectMsg(){

        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader =
                new MqttConnectVariableHeader(MqttVersion.MQTT_3_1.protocolName(), MqttVersion.MQTT_3_1.protocolLevel(),
                        true, false, false, 0, false, true, 5);

        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload("HIK", null, new byte[]{}, "LIGHT_001&LIGHT", new byte[]{});

        return new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
