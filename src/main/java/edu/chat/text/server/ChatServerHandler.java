package edu.chat.text.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Group Name:" + channels.name());

        Channel incoming = ctx.channel();

        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - [" + incoming.id() + "] has joined!\n");
        }
        System.out.println(incoming.id() + " added to Channel Group");
        channels.add(incoming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();

        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER - ]" + incoming.id() + " has left!\n");
        }
        channels.remove(incoming);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incomingChannel = ctx.channel();

        for (Channel channel : channels) {
            if (channel != incomingChannel)
                channel.writeAndFlush("[" + incomingChannel.id() + "] " + msg + "\r\n");
        }
    }
}
