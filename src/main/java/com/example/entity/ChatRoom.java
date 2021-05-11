package com.example.entity;

import com.example.utils.SocketMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private Integer id;

    private String name;

    private String password;

    private Integer hostId;

    private ServerSocket server;

    private final Set<Socket> sockets = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public void open() throws IOException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        server = new ServerSocket();
        server.bind(new InetSocketAddress(hostAddress, 0));
        Logger logger = LoggerFactory.getLogger(this.getClass());
        // 服务器线程
        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = server.accept();// 发送消息的人
                    // 加入房间
                    sockets.add(socket);
                    // 获取输入流
                    ObjectInputStream ois = SocketMap.getObjectInputStream(socket);

                    Thread readThread = new Thread(() -> {
                        while (true) {
                            try {
                                // 获取Message对象
                                Message message = (Message) ois.readObject();
                                if (message != null) {
                                    logger.info("服务器收到一条消息: " + message);
                                    // 转发
                                    for (Socket member : sockets) {
                                        // 获取输出流
                                        ObjectOutputStream oos = SocketMap.getObjectOutputStream(member);
                                        // 发送数据
                                        assert oos != null;
                                        oos.writeObject(message);
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
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
    }

    public void removeSocket(Socket socket) {
        for (Socket s : sockets) {
            if (s.getPort() == socket.getLocalPort()) {
                SocketMap.removeObjectOutputStream(s);
                SocketMap.removeObjectInputStream(s);
                sockets.remove(s);
                break;
            }
        }
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

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", hostId=" + hostId +
                '}';
    }
}