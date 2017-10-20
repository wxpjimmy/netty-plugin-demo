package com.jimmy.ratelimit.handler;

import com.jimmy.ratelimit.data.ErrorData;
import com.jimmy.ratelimit.data.TestData;
import org.jboss.netty.channel.*;

/**
 * Created by wangxiaopeng on 2017/10/20.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class ClientHandler extends SimpleChannelHandler {
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        if (e.getMessage() instanceof TestData) {
            System.out.println("Process succeed! " + e.getMessage());
        } else if(e.getMessage() instanceof ErrorData) {
            System.out.println("Error occurred! " + e.getMessage());
        } else {
            System.out.println("Received: " + e.getMessage());
        }
    }

    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("######channelConnected");
       // e.getChannel().write("hello server!");
        Channel ch = e.getChannel();
        for(int i=0;i<50; i++) {
            TestData td = new TestData("allen", 10+i);
            ch.write(td);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        System.out.println("Exception Occurred! " + e.getCause().getMessage());
        //Channels.write(ctx.getChannel(), e);
        e.getChannel().close();
    }

    public void writeComplete(
            ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        System.out.println("[]Write finished!");
    }
}
