package com.example.utils;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SocketMap {

    private static class Key {
        private final Integer roomId;
        private final Integer userId;

        public Key(Integer roomId, Integer userId) {
            this.roomId = roomId;
            this.userId = userId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((roomId == null) ? 0 : roomId.hashCode());
            result = prime * result + ((userId == null) ? 0 : userId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Key)) {
                return false;
            }
            Key key = (Key) obj;
            return Objects.equals(key.roomId, this.roomId) && Objects.equals(key.userId, this.userId);
        }
    }

    private static final Map<Key, Socket> socketMap = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectOutputStream> outputMap = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectInputStream> inputMap = new ConcurrentHashMap<>();

    public static void destroy() throws IOException {
        for (Map.Entry<Socket, ObjectOutputStream> entry : outputMap.entrySet()) {
            entry.getValue().close();
        }
        for (Map.Entry<Socket, ObjectInputStream> entry : inputMap.entrySet()) {
            entry.getValue().close();
        }
        for (Map.Entry<Key, Socket> entry : socketMap.entrySet()) {
            entry.getValue().close();
        }
    }

    public static void addSocket(Integer roomId, Integer userId, Socket socket) {
        Key key = new Key(roomId, userId);
        socketMap.put(key, socket);
    }

    public static void removeSocket(Integer roomId, Integer userId) {
        Key key = new Key(roomId, userId);
        socketMap.remove(key);
    }

    public static boolean containsSocket(Integer roomId, Integer userId) {
        Key key = new Key(roomId, userId);
        return socketMap.containsKey(key);
    }

    public static Socket getSocket(Integer roomId, Integer userId) {
        return socketMap.get(new Key(roomId, userId));
    }

    public static ObjectOutputStream getObjectOutputStream(Socket socket) throws IOException {
        if (outputMap.containsKey(socket)) {
            return outputMap.get(socket);
        }
        // 获取输出流
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        outputMap.put(socket, oos);
        return outputMap.put(socket, oos);
    }

    public static ObjectInputStream getObjectInputStream(Socket socket) throws IOException {
        if (inputMap.containsKey(socket)) {
            return inputMap.get(socket);
        }
        // 获取输入流
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        inputMap.put(socket, ois);
        return inputMap.put(socket, ois);
    }

    public static void removeObjectOutputStream(Socket socket) {
        outputMap.remove(socket);
    }

    public static void removeObjectInputStream(Socket socket) {
        inputMap.remove(socket);
    }
}
