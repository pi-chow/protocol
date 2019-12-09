package com.cetiti.iotp.protocol.alink.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * @author zhouliyu
 * @since 2019-11-26 10:40:51
 */
@ChannelHandler.Sharable
public class AlinkHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            System.out.println("服务端: " + msg);

            //接收消息写给发送者
            ctx.writeAndFlush(Unpooled.copiedBuffer("我是服务器，我收到了!", CharsetUtil.UTF_8))
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                System.out.println("write successful");
                            }else {

                                channelFuture.cause().printStackTrace();
                            }
                        }
                    });
        }finally {

            //显示释放资源
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /*ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE)*/;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
