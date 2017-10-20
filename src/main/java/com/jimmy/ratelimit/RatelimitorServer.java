package com.jimmy.ratelimit;

import com.jimmy.ratelimit.codec.DataDecoder;
import com.jimmy.ratelimit.codec.DataEncoder;
import com.jimmy.ratelimit.handler.RateLimitServerHandler;
import com.jimmy.ratelimit.handler.ServerHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by wangxiaopeng on 2017/10/18.
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */
public class RatelimitorServer {
    private ChannelFactory factory;

    public static ChannelGroup channelGroup = new DefaultChannelGroup();

    public void start(){
        // NioServerSocketChannelFactory用于创建基于NIO的服务端
        // ServerSocketChannel。本身包含2种线程，boss线程和worker线程。
        // 每个ServerSocketChannel会都会拥有自己的boss线程，
        // 当一个连接被服务端接受（accepted），
        // boss线程就会将接收到的Channel传递给一个worker线程处理，
        // 而worker线程以非阻塞的方式为一个或多个Channel提供非阻塞的读写
        factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),    // boss线程池
                Executors.newCachedThreadPool()); // worker线程数

        // ServerBootstrap用于帮助服务器启动
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        // 没有child.前缀，则该选项是为ServerSocketChannel设置
        //bootstrap.setOption("reuseAddress", true);
        // 有child.前缀，则该选项是为Channel设置
      bootstrap.setOption("child.tcpNoDelay", true);
      bootstrap.setOption("child.keepAlive", true);


        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("encoder", new DataEncoder());
                pipeline.addLast("decoder", new DataDecoder());
                pipeline.addLast("retelimit", new RateLimitServerHandler());
//                pipeline.addLast("decoder", new StringDecoder());
//                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("server", new ServerHandler());
                return pipeline;
            }
        });

        // 这里绑定服务端监听的IP和端口
        Channel channel = bootstrap.bind(new InetSocketAddress("127.0.0.1", 8010));
        RatelimitorServer.channelGroup.add(channel);

        System.out.println("Server is started...");
    }

    public void stop(){
        // ChannelGroup为其管理的Channels提供一系列的批量操作
        // 关闭的Channel会自动从ChannelGroup中移除
        ChannelGroupFuture channelGroupFuture = RatelimitorServer.channelGroup.close();
        channelGroupFuture.awaitUninterruptibly();
        factory.releaseExternalResources();
        System.out.println("Server is stopped.");
    }

    public static void main(String[] args) throws InterruptedException {
        RatelimitorServer server = new RatelimitorServer();
        server.start();

        Thread.sleep(300*1000);
        server.stop();
    }
}
