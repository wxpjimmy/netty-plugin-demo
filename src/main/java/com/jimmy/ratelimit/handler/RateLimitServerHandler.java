package com.jimmy.ratelimit.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.jimmy.ratelimit.data.TestData;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.nio.charset.Charset;

/**
 * Created by wangxiaopeng on 2017/10/18.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class RateLimitServerHandler extends SimpleChannelHandler{
    RateLimiter limiter = RateLimiter.create(10);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        if (limiter.tryAcquire()) {
            ctx.sendUpstream(e);
        } else {
            System.out.println("Rate limitor work!!!");
            if (e.getMessage() instanceof TestData) {
                TestData td = (TestData) e.getMessage();
                String failReason = "RateLimit";
                byte[] failReasonBinary = failReason.getBytes(Charset.forName("utf-8"));
                byte[] nameBinary = td.getName().getBytes(Charset.forName("utf-8"));
                ChannelBuffer buffer = ChannelBuffers.buffer(4+ 4 + failReasonBinary.length + 4 + 4 + nameBinary.length);
                buffer.writeInt(-1);
                buffer.writeInt(failReasonBinary.length);
                buffer.writeBytes(failReasonBinary);
                buffer.writeInt(nameBinary.length);
                buffer.writeBytes(nameBinary);
                buffer.writeInt(td.getAge());
                Channels.write(ctx.getChannel(), buffer);
            } else {
                System.out.println("Found unknown data!");
            }
        }
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ctx.sendDownstream(e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        System.out.println("Exception Occurred! " + e.getCause().getMessage());
        //Channels.write(ctx.getChannel(), e);
        e.getChannel().close();
    }
}
