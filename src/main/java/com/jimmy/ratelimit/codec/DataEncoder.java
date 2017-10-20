package com.jimmy.ratelimit.codec;

import com.jimmy.ratelimit.data.TestData;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.nio.charset.Charset;

/**
 * Created by wangxiaopeng on 2017/10/20.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class DataEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof ChannelBuffer) {
            return msg;
        } else {
            if (msg instanceof TestData) {
                TestData td = (TestData) msg;
                byte[] nameBinary = td.getName().getBytes(Charset.forName("utf-8"));
                ChannelBuffer buffer = ChannelBuffers.buffer(4 + 4 + nameBinary.length);
                buffer.writeInt(nameBinary.length);
                buffer.writeBytes(nameBinary);
                buffer.writeInt(td.getAge());
                return buffer;
            } else {
                System.out.println("Send Wrong Data!");
                return ChannelBuffers.EMPTY_BUFFER;
            }
        }
    }
}
