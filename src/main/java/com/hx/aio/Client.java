package com.hx.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Client {
    public static void main(String[] args) throws Exception {
        AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();
        clientChannel.connect(new InetSocketAddress("127.0.0.1",8888));



        System.out.println("AsynchronousClient starting.........");

        byte[] bytes = "hello async server...".getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, bytes.length);
        clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                ByteBuffer buffer1 = ByteBuffer.allocate(1024);
                clientChannel.read(buffer1, buffer1, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        byte[] array = attachment.array();
                        attachment.flip();
                        if(array.length != -1){
                            System.out.println("接受到服务端消息:" + new String(array, 0, array.length));
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.out.println("消息读取失败....");
                    }
                });

            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("消息发送失败....");
            }
        });

        while (true){
            Thread.sleep(1000);
        }
    }
}
