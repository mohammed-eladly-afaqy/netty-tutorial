package edu.voice.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;

public class VoiceClineHandler extends SimpleChannelInboundHandler<ByteBuf> {

    String filePath = "/home/afaqy/Music/ringtone.mp3";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public byte[] readMp3FileIoUtils() throws IOException {
        byte[] bytes = null;
        bytes = IOUtils.toByteArray(new FileInputStream(new File(filePath)));
        return bytes;

    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("Client Received Bytes Size: " + msg.readableBytes());


        byte[] bytes = new byte[msg.readableBytes()];
        msg.getBytes(msg.readerIndex(), bytes);

        outputStream.write(bytes);
//        System.out.println("Client stream: " + outputStream.size());

    }

    byte[][] splitArrays(byte[] data) {
        int chunkSize = 1024;

        final int length = data.length;
        final byte[][] dest = new byte[(length + chunkSize - 1) / chunkSize][];
        int destIndex = 0;
        int stopIndex = 0;

        for (int startIndex = 0; startIndex + chunkSize <= length; startIndex += chunkSize) {
            stopIndex += chunkSize;
            dest[destIndex++] = Arrays.copyOfRange(data, startIndex, stopIndex);
        }

        if (stopIndex < length)
            dest[destIndex] = Arrays.copyOfRange(data, stopIndex, length);

        return dest;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        byte[] bytes = readMp3FileIoUtils();//10000000

        System.out.println("Sending to Server Bytes Size: " + bytes.length);

        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        System.out.println(byteBuf.readableBytes());
        ctx.writeAndFlush(byteBuf);
//        byte[][] splitArrays = splitArrays(bytes);
//
//        for (byte[] array : splitArrays) {
//            System.out.println("Send Bytes: " + array.length);
//            ctx.writeAndFlush(Unpooled.copiedBuffer(array));
//        }


    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client Complete");
        outputStream.close();
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);

//        writeMp3File(outputStream.toByteArray());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
