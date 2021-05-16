package com.example.entity;

import com.example.service.UserService;
import com.example.utils.CustomConstant;
import com.example.utils.SocketMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

@Component
public class Server {
    static {
        try {
            open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ServerSocket serverSocket;

    private static UserService userService;

    public static String hostAddress;
    public static Integer localPort;

    @Autowired
    public Server(UserService userService) {
        Server.userService = userService;
    }


    public static void open() throws IOException {
        hostAddress = InetAddress.getLocalHost().getHostAddress();
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostAddress, 0));
        localPort = serverSocket.getLocalPort();
        Logger logger = LoggerFactory.getLogger(Server.class);
        // 服务器线程
        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();// 发送消息的人
                    // 获取输入流
                    ObjectInputStream ois = SocketMap.getObjectInputStream(socket);
                    // 获取连接信息
                    Message linkMsg = (Message) ois.readObject();
                    // 保存
                    SocketMap.addAcceptSocket(linkMsg.getSender().getId(), socket);

                    Thread readThread = new Thread(() -> {
                        while (true) {
                            try {
                                // 获取Message对象
                                Message message = (Message) ois.readObject();
                                if (message != null) {
                                    logger.info("服务器收到一条消息: " + message);
                                    Integer senderId = message.getSender().getId();
                                    User receiver = message.getReceiver();
                                    Integer receiverId = receiver == null ? 0 : message.getReceiver().getId();
                                    // -----转发-----
                                    // 获取活跃的用户
                                    List<User> users = userService.getUsersByRoomIdActive(message.getRoomId(), CustomConstant.activeTime);
                                    for (User user : users) {
                                        if (receiverId != 0 && !senderId.equals(user.getId()) && !receiverId.equals(user.getId())) {
                                            continue;
                                        }
                                        Socket member = SocketMap.getAcceptSocket(user.getId());
                                        if (member != null) {
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
                            } catch (IOException e) {
//                                e.printStackTrace();
                                break;
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    readThread.start();
                } catch (SocketException e) {
//                    e.printStackTrace();
                    break;
                } catch (IOException e) {
//                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
    }
}
