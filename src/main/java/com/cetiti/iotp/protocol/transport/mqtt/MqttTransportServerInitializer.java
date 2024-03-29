package com.cetiti.iotp.protocol.transport.mqtt;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

/**
 * MQTT Handler初始化
 * @author zhouliyu
 * @since 2019-12-05 18:58:56
 */
public class MqttTransportServerInitializer extends ChannelInitializer<SocketChannel> {

    private final  MqttTransportContext context;

    public MqttTransportServerInitializer(MqttTransportContext context) {
        this.context = context;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        //TODO sslHandler

        pipeline.addLast("decoder", new MqttDecoder(context.getMaxPayloadSize()));

        pipeline.addLast("encoder", MqttEncoder.INSTANCE);

        MqttTransportHandler mqttTransportHandler = new MqttTransportHandler(context);

        pipeline.addLast(mqttTransportHandler);

        ch.closeFuture().addListener(mqttTransportHandler);


    }
}
