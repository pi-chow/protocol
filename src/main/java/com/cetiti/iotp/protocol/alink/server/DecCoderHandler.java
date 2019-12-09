package com.cetiti.iotp.protocol.alink.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouliyu
 * @since 2019-11-27 14:40:51
 */
@ChannelHandler.Sharable
public class DecCoderHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private static final Logger LOGGER = LoggerFactory.getLogger(DecCoderHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {


        byte[] msgBytes = new byte[byteBuf.readableBytes()];

        byteBuf.getBytes(byteBuf.readerIndex(), msgBytes);

        String msg = new String(msgBytes);

        LOGGER.info("Receive Alink mqttMessage[{}].", msg);

        ctx.fireChannelRead(msg);


    }
}
