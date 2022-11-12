package com.hx.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress("127.0.0.1",8888));
        ExecutorService executorService = Executors.newCachedThreadPool();
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);

        System.out.println("AsynchronousServer starting...........");
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>(){
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                serverChannel.accept(null, this);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {

                        byte[] msg = attachment.array();
                        attachment.flip();
                        if(msg.length != -1){
                            System.out.println("收到客户端消息:" + new String(msg, 0, msg.length));
                        }
                        client.write(ByteBuffer.wrap("i am AsynchronousServer....".getBytes()));
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        exc.printStackTrace();
                    }
                });

            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        while (true){
            Thread.sleep(1000);
        }
    }
}
