package com.hx.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel ssc = SocketChannel.open();
        ssc.configureBlocking(false);
        ssc.connect(new InetSocketAddress("127.0.0.1",8888));


        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_CONNECT);

        while (true){
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isConnectable()){
                    SocketChannel sc = (SocketChannel)key.channel();
                    if(sc.isConnectionPending()){
                        sc.finishConnect();
                    }
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.wrap("im client".getBytes());
                    sc.write(buffer);
                    sc.register(selector, SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = sc.read(buffer);
                    if(len != -1){
                        System.out.println("客户端收到信息:" + new String(buffer.array(), 0, len));
                    }
                }
            }
        }

    }
}
