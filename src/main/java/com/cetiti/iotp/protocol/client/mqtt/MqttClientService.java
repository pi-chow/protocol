package com.cetiti.iotp.protocol.client.mqtt;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * MQTT 客户端服务
 * @author zhouliyu
 * @since 2019-12-12 15:18:53
 */
@Slf4j
@Service
public class MqttClientService {

    @Value("${client.mqtt.bind_host}")
    private String host;

    @Value("${client.mqtt.bind_port}")
    private Integer port;

    @Value("${client.mqtt.reconnect}")
    private boolean reconnect;

    @Value("${client.mqtt.reconnectDelay}")
    private long reconnectDelay;

    @Autowired
    private MqttClientContext context;

    private Channel clientChannel;

    private EventLoopGroup bossGroup;

    private volatile boolean disconnected = false;

    @Setter
    private MqttClientCallback callback = new MqttClientCallbackImpl();

    @PostConstruct
    public void init(){

        log.info("Starting MQTT Client...");
        Future<MqttConnectResult> connectFuture = connect(this.host, this.port);

        try {
            log.info("MQTT Client Connected: [{}]", connectFuture.get(500, TimeUnit.MILLISECONDS).toString());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            connectFuture.cancel(true);
        }

    }

    private Future<MqttConnectResult> connect(String host, int port){

        if (bossGroup == null) {
            bossGroup = new NioEventLoopGroup();
        }

        this.host = host;
        this.port = port;

        Promise<MqttConnectResult> connectFuture = new DefaultPromise<>(bossGroup.next());
        context.setConnectFuture(connectFuture);
        Bootstrap b = new Bootstrap();
        b.group(bossGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .handler(new MqttChannelInitializer(context));

        ChannelFuture future;
        try {
            future = b.connect().sync();
        }catch (Exception e) {
            log.warn("Failed connect server [{}:{}] ", host, port, e);
            return connectFuture;
        }

        clientChannel = future.channel();

        //监听器
        future.addListener( f -> {

            if (f.isSuccess()) {

                clientChannel.closeFuture().addListener(closeFuture->{

                    if (isConnected()) {
                        return;
                    }

                    if (callback != null) {

                        callback.connectionLost(new RuntimeException("Channel is closed"));
                    }

                    //关闭重连
                    scheduleConnectIfRequest(host, port, true);

                });

            }else {
                scheduleConnectIfRequest(host, port, reconnect);
            }

        });

        return connectFuture;
    }

    private boolean isConnected(){

        return !disconnected && clientChannel != null && clientChannel.isActive();
    }

    private void scheduleConnectIfRequest(String host, int port, boolean reconnect){

        if (reconnect && !disconnected) {

            bossGroup.schedule(() -> connect(host, port), reconnectDelay, TimeUnit.SECONDS);

        }


    }


    @PreDestroy
    public void shutdown() throws InterruptedException {

        log.info("Stopping MQTT Client!");
        try {
            clientChannel.close().sync();
        }finally {
            bossGroup.shutdownGracefully();
        }
        log.info("MQTT Client stopped!");

    }

    private class MqttClientCallbackImpl implements MqttClientCallback{

        @Override
        public void connectionLost(Throwable cause) {
            try {
                shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
