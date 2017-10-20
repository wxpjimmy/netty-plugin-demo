package com.jimmy.ratelimit.handler;

import org.jboss.netty.channel.*;

/**
 * Created by wangxiaopeng on 2017/10/20.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class ServerHandler extends SimpleChannelHandler {
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        System.out.println("Received Data: " + e.getMessage());
        Channels.write(ctx.getChannel(), e.getMessage());
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }

    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("ChannelConnected##########################");
        ctx.sendUpstream(e);
    }

    public void writeComplete(
            ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        System.out.println("Write finished!");
        ctx.sendUpstream(e);
    }
}