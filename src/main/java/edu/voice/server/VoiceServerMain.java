package edu.voice.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class VoiceServerMain {

    final int port;

    public VoiceServerMain(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new VoiceServerMain(9999).start();
    }

    public void start() {

        final VoiceServerHandler voiceServerHandler = new VoiceServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        InetSocketAddress socketAddress = new InetSocketAddress(port);

        serverBootstrap.group(group).channel(NioServerSocketChannel.class)
                .localAddress(socketAddress)

                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(voiceServerHandler);
                    }
                });
        ChannelFuture channelFuture;
        try {
            channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
