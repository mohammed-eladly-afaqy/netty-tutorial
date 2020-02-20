package edu.voice.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;

@ChannelHandler.Sharable
public class VoiceServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
   static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        System.out.println("Server Received Bytes Size: " + msg.readableBytes());

        byte[] bytes = new byte[msg.readableBytes()];
        msg.getBytes(msg.readerIndex(), bytes);

        outputStream.write(bytes);

    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        System.out.println("server complete");
        outputStream.close();
        writeMp3File(outputStream.toByteArray());
    }

    public void writeMp3File(byte[] bytes) {
        File someFile = new File("output.mp3");
        FileOutputStream fos;

        try {

            fos = new FileOutputStream(someFile);

            fos.write(bytes);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
