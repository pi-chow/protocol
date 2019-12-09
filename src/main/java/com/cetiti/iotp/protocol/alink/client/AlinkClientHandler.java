package com.cetiti.iotp.protocol.alink.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import java.util.concurrent.*;

/**
 * @author zhouliyu
 * @since 2019-11-26 11:05:06
 */
@ChannelHandler.Sharable
public class AlinkClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {

            Thread thread = new Thread(r);

            thread.setDaemon(true);

            thread.setName("thread-pool-write");

            return thread;
        }
    });

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.copiedBuffer("Alink Client connect.... !", CharsetUtil.UTF_8));

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ctx.writeAndFlush(Unpooled.copiedBuffer("Alink Client Data Rocks !", CharsetUtil.UTF_8));
            }
        }, 2, 5 , TimeUnit.SECONDS);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
