package com.example.entity;

import com.example.service.UserService;
import com.example.utils.SocketMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

@Component
public class Server {
    private static ServerSocket serverSocket;

    private static UserService userService;

    static {
        try {
            open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        Server.userService = userService;
    }


    public static String getAddress() {
        return serverSocket.getInetAddress().getHostAddress();
    }

    public static Integer getPort() {
        return serverSocket.getLocalPort();
    }

    private static void open() throws IOException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostAddress, 0));
        Logger logger = LoggerFactory.getLogger(Server.class);
        // 服务器线程
        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();// 发送消息的人

                    // 获取输入流
                    ObjectInputStream ois = SocketMap.getObjectInputStream(socket);

                    Thread readThread = new Thread(() -> {
                        while (true) {
                            try {
                                // 获取Message对象
                                Message message = (Message) ois.readObject();
                                if (message != null) {
                                    int roomId = message.getRoomId();
                                    logger.info("服务器收到一条消息: " + message);
                                    List<User> users = userService.getUsersByRoomId(roomId);
                                    // 转发
                                    for (User user : users) {
                                        Integer userId = user.getId();
                                        Socket member = SocketMap.getSocket(roomId, userId);
                                        if (member != null) { // 在线
                                            // 获取输出流
                                            ObjectOutputStream oos = SocketMap.getObjectOutputStream(member);
                                            // 发送数据
                                            assert oos != null;
                                            oos.writeObject(message);
                                        }
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

    public static void close() {
        try {
            SocketMap.destroy();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
