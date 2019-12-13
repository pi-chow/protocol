package com.cetiti.iotp.protocol.client.mqtt;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_ACCEPTED;

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
                processConnectAck(ctx, (MqttConnAckMessage) msg);
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

    private void processConnectAck(ChannelHandlerContext ctx, MqttConnAckMessage msg){

        log.info("processing connect ack msg: {}!", msg.variableHeader().connectReturnCode());

        switch (msg.variableHeader().connectReturnCode()) {

            case CONNECTION_ACCEPTED:
                connectFuture.setSuccess(new MqttConnectResult(true, CONNECTION_ACCEPTED, ctx.channel().closeFuture()));
                break;
            default:
                connectFuture.setSuccess(new MqttConnectResult(false, msg.variableHeader().connectReturnCode(), ctx.channel().closeFuture()));
                ctx.close();
                break;
        }



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
