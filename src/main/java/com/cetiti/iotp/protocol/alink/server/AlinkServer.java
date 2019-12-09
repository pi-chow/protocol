package com.cetiti.iotp.protocol.alink.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author zhouliyu
 * @since 2019-11-26 10:08:00
 */
public class AlinkServer {

    private static final String TAG = "AlinkServer";

    private static final Logger LOGGER = LoggerFactory.getLogger(AlinkServer.class);

    private final int port;

    public AlinkServer(int port) {
        this.port = port;
    }


    public static void main(String[] args) throws InterruptedException {

        if (args.length != 1) {

            LOGGER.error(TAG + " args error");

        }

        int port = Integer.parseInt(args[0]);

        new AlinkServer(port).start();
    }


    private void start() throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                //handler初始化执行
                .handler(new LoggingHandler(LogLevel.INFO))
                //childHandler会在客户端成功Connect后执行
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {

                        ChannelPipeline channelPipeline = channel.pipeline();
                        channelPipeline.addFirst("idle", new IdleStateHandler(0, 0, 60));

                        //SSL


                        channelPipeline.addLast(new DecCoderHandler());
                        channelPipeline.addLast(new AlinkHandler());

                    }
                });
        try {
            ChannelFuture future = b.bind().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //关闭EventLoopGroup释放的所有资源
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }


    }

}
