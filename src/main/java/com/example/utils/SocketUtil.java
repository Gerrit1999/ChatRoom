package com.example.utils;

import com.example.entity.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketUtil {
    public static Socket createSocket(int timeout) throws IOException {
        Socket socket = new Socket(Server.getAddress(), Server.getPort());
        // 设置超时时间
        socket.setSoTimeout(timeout);
        return socket;
    }

    public static ObjectOutputStream createObjectOutputStream(Socket socket) throws IOException {
        // 获取输出流
        return new ObjectOutputStream(socket.getOutputStream());
    }
}
