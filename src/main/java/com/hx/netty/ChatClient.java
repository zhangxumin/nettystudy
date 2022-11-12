package com.hx.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ChatClient {
    private String ip;
    private int port;

    private Channel channel;

    private ClientFrame clientFrame;

    public ChatClient(ClientFrame clientFrame, String ip, int port) {
        this.clientFrame = clientFrame;
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws Exception {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        ChannelFuture future = b.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ClientHandler clientHandler = new ClientHandler(clientFrame);
                        ch.pipeline().addLast(clientHandler);
                    }
                })
                .connect(ip, port).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        System.out.println("client connect success.....");
                        channel = future.channel();
                    }
                }).sync();
                future.channel().closeFuture().sync();

    }

    public void sendMsg(String msg){
        channel.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter{
    private ClientFrame clientFrame;

    public ClientHandler(ClientFrame clientFrame) {
        this.clientFrame = clientFrame;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channelActive.....");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = null;
        try {
            byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);

            String strMsg = new String(bytes,CharsetUtil.UTF_8);
            System.out.println("接受到服务端消息:"+ strMsg);
            clientFrame.updateTextArea(strMsg);
        } finally {
            if (byteBuf != null) {
                ReferenceCountUtil.release(byteBuf);
            }
        }
    }

}


