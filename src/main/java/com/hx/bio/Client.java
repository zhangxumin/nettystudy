package com.hx.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            outputStream = socket.getOutputStream();
            outputStream.write(new String("客户端").getBytes());
            outputStream.flush();

            inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int length = inputStream.read(bytes);
            String msg = new String(bytes, 0, length);
            System.out.println("msg = " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream != null) outputStream.close();
                if(inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
