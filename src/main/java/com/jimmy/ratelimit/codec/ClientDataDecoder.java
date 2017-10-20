package com.jimmy.ratelimit.codec;

import com.jimmy.ratelimit.data.ErrorData;
import com.jimmy.ratelimit.data.TestData;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.nio.charset.Charset;

/**
 * Created by wangxiaopeng on 2017/10/20.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class ClientDataDecoder extends FrameDecoder {
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < 4) {
            return null;
        }

        int len = buffer.readInt();
        if (len == -1) { //met Rate Limit error
            if (buffer.readableBytes() < 4) {
                buffer.resetReaderIndex();
                return null;
            }
            len = buffer.readInt(); //read error msg length
            if (buffer.readableBytes() < (len + 4)) {
                buffer.resetReaderIndex();
                return null;
            }
            byte[] data = new byte[len];
            buffer.readBytes(data);
            int nameLen = buffer.readInt();
            if (buffer.readableBytes() < (nameLen + 4)) {
                buffer.resetReaderIndex();
                return null;
            }
            byte[] nameData = new byte[nameLen];
            buffer.readBytes(nameData);
            String name = new String(nameData, Charset.forName("utf-8"));
            String errorMsg = new String(data, Charset.forName("utf-8"));
            int age = buffer.readInt();
            return new ErrorData(errorMsg, new TestData(name, age));
        } else {
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
    }
}
