package com.example.entity;

import com.example.utils.SocketMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket server;

    static {
        try {
            open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAddress() {
        return server.getInetAddress().getHostAddress();
    }

    public static Integer getPort() {
        return server.getLocalPort();
    }

    private static void open() throws IOException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        server = new ServerSocket();
        server.bind(new InetSocketAddress(hostAddress, 0));
        // 服务器线程
        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = server.accept();// 发送消息的人

                    // 获取输入流
                    ObjectInputStream ois = SocketMap.getObjectInputStream(socket);

                    Thread readThread = new Thread(() -> {
                        while (true) {
                            try {
                                // 获取Message对象
                                Message message = (Message) ois.readObject();
                                int roomId = message.getRoomId();
                                if (message != null) {
                                    System.out.println("服务器收到一条消息: " + message);
                                    // 转发
                                    /*for (Socket member : sockets) {
                                        // 获取输出流
                                        ObjectOutputStream oos = SocketMap.getObjectOutputStream(member);
                                        // 发送数据
                                        assert oos != null;
                                        oos.writeObject(message);
                                    }*/
                                } else {
                                    break;
                                }
                            } catch (StreamCorruptedException e) {
                                System.out.println("未知消息");
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    readThread.start();
                } catch (IOException e) {
                    // e.printStackTrace();
                    break;
                }
            }
        });
        serverThread.start();
    }

    public static void close() {
        try {
            SocketMap.destroy();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
