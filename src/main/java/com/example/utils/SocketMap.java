package com.example.utils;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SocketMap {

    private static final Map<Integer, Socket> acceptSocket = new ConcurrentHashMap<>();
    private static final Map<Integer, Socket> connectSocket = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectOutputStream> outputMap = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectInputStream> inputMap = new ConcurrentHashMap<>();

    public static void destroy() throws IOException {
        for (Map.Entry<Socket, ObjectOutputStream> entry : outputMap.entrySet()) {
            entry.getValue().close();
        }
        for (Map.Entry<Socket, ObjectInputStream> entry : inputMap.entrySet()) {
            entry.getValue().close();
        }
        for (Map.Entry<Integer, Socket> entry : acceptSocket.entrySet()) {
            entry.getValue().close();
        }
        for (Map.Entry<Integer, Socket> entry : connectSocket.entrySet()) {
            entry.getValue().close();
        }
    }

    public static void addAcceptSocket(Integer userId, Socket socket) {
        acceptSocket.put(userId, socket);
    }

    public static void addConnectSocket(Integer userId, Socket socket) {
        connectSocket.put(userId, socket);
    }

    public static void removeAcceptSocket(Integer userId) throws IOException {
        if (containsAcceptSocket(userId)) {
            Socket socket = acceptSocket.remove(userId);
            socket.close();
        }
    }

    public static void removeConnectSocket(Integer userId) throws IOException {
        if (containsConnectSocket(userId)) {
            Socket socket = connectSocket.remove(userId);
            socket.close();
        }
    }

    public static boolean containsAcceptSocket(Integer userId) {
        return acceptSocket.containsKey(userId);
    }

    public static boolean containsConnectSocket(Integer userId) {
        return connectSocket.containsKey(userId);
    }

    public static Socket getAcceptSocket(Integer userId) {
        return acceptSocket.get(userId);
    }

    public static Socket getConnectSocket(Integer userId) {
        return connectSocket.get(userId);
    }

    public static ObjectOutputStream getObjectOutputStream(Socket socket) throws IOException {
        if (outputMap.containsKey(socket)) {
            return outputMap.get(socket);
        }
        // 获取输出流
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        outputMap.put(socket, oos);
        return oos;
    }


    public static ObjectInputStream getObjectInputStream(Socket socket) throws IOException {
        if (inputMap.containsKey(socket)) {
            return inputMap.get(socket);
        }
        // 获取输入流
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        inputMap.put(socket, ois);
        return ois;
    }

    public static void removeObjectOutputStream(Socket socket) {
        outputMap.remove(socket);
    }

    public static void removeObjectInputStream(Socket socket) {
        inputMap.remove(socket);
    }
}
