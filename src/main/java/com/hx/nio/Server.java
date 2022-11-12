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

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8888));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("serverSocketChannel starting.............");
        while (true){
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                myHandle(key);
            }
        }
    }

    private static void myHandle(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(key.selector(), SelectionKey.OP_READ);
        }else if(key.isReadable()){
           SocketChannel sc = (SocketChannel) key.channel();
           ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

           int len = sc.read(byteBuffer);
           String msg = new String(byteBuffer.array(), 0, len);
           if(len != -1){
               System.out.println("服务端收到信息:" + msg);
           }
           sc.register(key.selector(), SelectionKey.OP_WRITE);
        }else if(key.isWritable()){
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bufferToWrite = ByteBuffer.wrap("i am server...".getBytes());
            sc.write(bufferToWrite);
            sc.register(key.selector(), SelectionKey.OP_READ);
        }
    }
}
