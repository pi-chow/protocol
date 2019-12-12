package com.cetiti.iotp.protocol.transport.mqtt;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * MQTT 服务端
 * @author zhouliyu
 * @since 2019-12-05 16:20:35
 */
@Slf4j
@Service
public class MqttTransportService {

    @Value("${transport.mqtt.bind_address}")
    private String host;

    @Value("${transport.mqtt.bind_port}")
    private Integer port;

    @Value("${transport.mqtt.netty.leak_detector_level}")
    private String leakDetectorLevel;

    @Value("${transport.mqtt.netty.boss_group_thread_count}")
    private Integer bossGroupThreadCount;

    @Value("${transport.mqtt.netty.worker_group_thread_count}")
    private Integer workerGroupThreadCount;

    @Value("${transport.mqtt.netty.so_keep_alive}")
    private boolean keepAlive;

    @Autowired
    private MqttTransportContext context;

    private Channel serverChannel;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @PostConstruct
    public void init() throws InterruptedException {

        log.info("Setting resource leak detector level to {}", leakDetectorLevel);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetectorLevel));

        log.info("Starting MQTT transport...");
        bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = new NioEventLoopGroup(workerGroupThreadCount);

        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new MqttTransportServerInitializer(context))
                .childOption(ChannelOption.SO_KEEPALIVE, keepAlive);

        serverChannel = b.bind(host, port).sync().channel();
        log.info("MQTT transport started");

    }

    @PreDestroy
    public void shutdown() throws InterruptedException {

        log.info("Stopping MQTT transport!");
        try {
            serverChannel.close().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        log.info("MQTT transport stopped!");



    }


}
