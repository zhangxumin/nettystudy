package com.hx.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("127.0.0.1", 8888));
            System.out.println("server starting.....");
            while (true){
                Socket s = serverSocket.accept();
                new Thread(() -> myHandle(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void myHandle(Socket s) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = s.getInputStream();
            byte[] bytes = new byte[1024];
            int length = inputStream.read(bytes);
            String msg = new String(bytes, 0, length);
            System.out.println("server = " + msg);

            outputStream = s.getOutputStream();
            outputStream.write(new String(msg + " copy it from server").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       
    }
}
