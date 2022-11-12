package com.hx.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PollServer {
    private ExecutorService executorService;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;


    public void init() throws IOException {
        executorService = Executors.newFixedThreadPool(3);
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(8888));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public static void main(String[] args) throws IOException {
        PollServer ps = new PollServer();
        ps.init();
        System.out.println("pollServer starting.............");
        while (true){
            ps.listen();
        }
    }

    public void listen() throws IOException {
        selector.select();
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            iterator.remove();
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();//此处要注册的是读通道
                sc.configureBlocking(false);
                sc.register(key.selector(), SelectionKey.OP_READ);
            }else if(key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                sc.register(key.selector(), SelectionKey.OP_WRITE);
                executorService.execute(new MyHandle(key));
            }
        }
    }

    private class MyHandle extends Thread {
        private SelectionKey key;

        public MyHandle(SelectionKey key) {
            this.key = key;
        }

        @Override
        public void run() {
            try {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int len = sc.read(byteBuffer);
                String msg = new String(byteBuffer.array(), 0, len);
                if (len != -1) {
                    System.out.println("PollServer接收到消息：" + msg);
                }

                ByteBuffer bufferToWrite = ByteBuffer.wrap("i am pollserver...".getBytes());
                sc.write(bufferToWrite);
                bufferToWrite.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
