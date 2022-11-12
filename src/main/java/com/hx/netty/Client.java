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

public class Client {
    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup workers = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            ChannelFuture future = bootstrap.group(workers)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new myChannelHandler());
                        }
                    })
                    .connect("127.0.0.1", 8888)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                System.out.println("connected is failed...........");
                            } else {
                                System.out.println("connected success............");
                            }
                        }
                    }).sync();//此处不阻塞，程序会继续往下执行，由于connect方法也是异步的，程序会直接结束掉，导致失败
            future.channel().closeFuture().sync();//这句很重要
        } finally {
            workers.shutdownGracefully();
        }
    }
}

class myChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture fu = ctx.writeAndFlush(Unpooled.copiedBuffer("hello netty server".getBytes()));
        fu.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("消息发送成功.....");
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("接收到服务端消息:" + byteBuf.toString(CharsetUtil.UTF_8));
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}


