package com.example.utils;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketMap {
    private static class Key {
        private final String sessionId;
        private final Integer port;

        public Key(String sessionId, Integer port) {
            this.sessionId = sessionId;
            this.port = port;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
            result = prime * result + ((port == null) ? 0 : port.hashCode());
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
            return key.port.equals(this.port) && key.sessionId != null && key.sessionId.equals(this.sessionId);
        }
    }

    private static final Map<Key, Socket> socketMap = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectOutputStream> outputMap = new ConcurrentHashMap<>();
    private static final Map<Socket, ObjectInputStream> inputMap = new ConcurrentHashMap<>();

    public static void addSocket(String sessionId, Integer port, Socket socket) {
        Key key = new Key(sessionId, port);
        socketMap.put(key, socket);
    }

    public static void removeSocket(String sessionId, Integer port) {
        Key key = new Key(sessionId, port);
        socketMap.remove(key);
    }

    public static Socket getSocket(String sessionId, Integer port) {
        return socketMap.get(new Key(sessionId, port));
    }

    public static ObjectOutputStream getObjectOutputStream(Socket socket) {
        if (outputMap.containsKey(socket)) {
            return outputMap.get(socket);
        }
        try {
            // 获取输出流
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            outputMap.put(socket, oos);
            return outputMap.put(socket, oos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public static boolean containsSocket(String sessionId, Integer port) {
        return socketMap.containsKey(new Key(sessionId, port));
    }
}
