package com.jimmy.ratelimit.codec;

import com.jimmy.ratelimit.data.TestData;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.nio.charset.Charset;

/**
 * Created by wangxiaopeng on 2017/10/20.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class DataDecoder extends FrameDecoder {
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < 4) {
            return null;
        }

        int len = buffer.readInt();
        if (buffer.readableBytes() < (len + 4)) {
            buffer.resetReaderIndex();
            return null;
        } else {
            byte[] content = new byte[len];
            buffer.readBytes(content);
            int age = buffer.readInt();
            String name = new String(content, Charset.forName("utf-8"));
            return new TestData(name, age);
        }
    }

    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("Decode failed!");
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
