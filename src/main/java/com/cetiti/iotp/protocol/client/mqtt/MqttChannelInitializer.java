package com.cetiti.iotp.protocol.client.mqtt;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

/**
 * @author zhouliyu
 * @since 2019-12-12 15:36:22
 */
public class MqttChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final MqttClientContext context;

    public MqttChannelInitializer(MqttClientContext context) {
        this.context = context;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("decoder", new MqttDecoder(context.getMaxPayloadSize()));
        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
        pipeline.addLast("mqttClientHandler", new MqttClientHandler(context.getConnectFuture()));

    }
}
