package com.cetiti.iotp.protocol.alink.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouliyu
 * @since 2019-11-26 10:08:00
 */
public class AlinkClient {

    private final String host;

    private final int port;

    public AlinkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {

        if (args.length != 2) {

            System.err.println("error <host> <port>");

            return;

        }

        String host = args[0];

        int port = Integer.parseInt(args[1]);

        new AlinkClient(host, port).start();
    }


    private void start() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {

                        channel.pipeline().addLast(new AlinkClientHandler());

                    }
                });
        try {
            ChannelFuture future = b.connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //关闭EventLoopGroup释放的所有资源
            group.shutdownGracefully().sync();
        }


    }

}
