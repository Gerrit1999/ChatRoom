package com.example.entity;

import com.example.utils.SocketMap;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom implements Serializable {
    private ServerSocket server;// 服务器socket

    private final Set<Socket> sockets;// 客户端socket集合

    public ChatRoom(ServerSocket server) {
        this.server = server;
        sockets = new HashSet<>();
    }

    public void addSocket(Socket socket) {
        sockets.add(socket);
    }

    public void removeSocket(Socket socket) {
        for (Socket s : sockets) {
            if (s.getPort() == socket.getLocalPort()) {
                sockets.remove(s);
                break;
            }
        }
        if (sockets.isEmpty()) {
            close();
        }
    }

    public void open() {
        // 服务器线程
        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = server.accept();// 发送消息的人
                    addSocket(socket);// 加入房间

                    // 获取输入流
                    ObjectInputStream ois = SocketMap.getObjectInputStream(socket);

                    Thread readThread = new Thread(() -> {
                        while (true) {
                            try {
                                // 获取Message对象
                                Message message = (Message) ois.readObject();
                                if (message != null) {
                                    System.out.println("服务器收到一条消息: " + message);
                                    // 转发
                                    for (Socket member : sockets) {
                                        // 获取输出流
                                        ObjectOutputStream oos = SocketMap.getObjectOutputStream(member);
                                        // 发送数据
                                        oos.writeObject(message);
                                    }
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

    public void close() {
        try {
            for (Socket socket : sockets) {
                socket.close();
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }
}
