package com.example.controller;

import com.example.entity.ChatRoom;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.service.ChatRoomService;
import com.example.service.UserService;
import com.example.utils.ChatRoomMap;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import com.example.utils.SocketMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

@Controller
@RequestMapping("/chatRoom")
public class ChatRoomController {
    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    UserService userService;

    private static final int timeout = 20 * 1000;

    /**
     * 创建房间
     */
    @ResponseBody
    @RequestMapping("/create/chatRoom.json")
    public ResultEntity<Integer> createChatRoom(@RequestBody ChatRoom chatRoom) {
        // 创建房间
        chatRoomService.addChatRoom(chatRoom);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, chatRoom.getId());
    }

    /**
     * 加入房间
     */
    @ResponseBody
    @RequestMapping("/join/chatRoom.json")
    public ResultEntity<ChatRoom> toChatRoom(@RequestParam("userId") Integer userId,
                                             @RequestParam("roomId") Integer roomId,
                                             @RequestParam("password") String password) {
        ChatRoom chatRoom;

        if (ChatRoomMap.containsChatRoom(roomId)) {    // 服务器已开启该房间
            chatRoom = ChatRoomMap.getChatRoom(roomId);
        } else {
            // 获取房间
            chatRoom = chatRoomService.getChatRoomById(roomId);
            if (chatRoom == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
            }
            ChatRoomMap.addChatRoom(chatRoom.getId(), chatRoom);
            try {
                chatRoom.open();    // 开启房间服务器
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 加入房间失败
        if (!chatRoomService.judgeUserInRoom(roomId, userId) && !chatRoomService.addUserToChatRoom(roomId, userId, password)) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }

        try {
            // 连接到房间
            Socket socket = SocketMap.getSocket(roomId, userId);
            if (socket == null) {
                ServerSocket server = chatRoom.getServer();
                // 连接到房间
                socket = new Socket(server.getInetAddress().getHostAddress(), server.getLocalPort());
                // 设置超时时间
                socket.setSoTimeout(timeout);
                // 保存socket
                SocketMap.addSocket(roomId, userId, socket);
            }
            // 获取用户名
            String username = userService.getUsernameById(userId);
            // 提示信息
            String msg = username + " 已进入房间";
            // 封装数据
            User user = userService.getUserById(userId);
            Message message = new Message(msg, roomId, user);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            if (oos == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION + "ObjectOutputStream", null);
            }
            // 发送数据
            oos.writeObject(message);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, chatRoom);
        } catch (IOException | NullPointerException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    /**
     * 退出房间
     */
    @ResponseBody
    @RequestMapping("/do/exit.json")
    public ResultEntity<String> doExit(@RequestParam("roomId") Integer roomId,
                                       @RequestParam("userId") Integer userId) {
        // 退出房间
        if (!chatRoomService.removeUserFromChatRoom(roomId, userId)) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }

        ChatRoom chatRoom = ChatRoomMap.getChatRoom(roomId);
        if (chatRoom == null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
        }

        try {
            // 获取用户名
            String username = userService.getUsernameById(userId);
            // 提示信息
            String msg = username + " 已退出房间";
            // 封装数据
            User user = userService.getUserById(userId);
            Message message = new Message(msg, roomId, user);
            // 获取socket
            Socket socket = SocketMap.getSocket(roomId, userId);
            if (socket == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
            }
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 发送数据
            oos.writeObject(message);
            // 删除socket
            SocketMap.removeSocket(roomId, userId);
            chatRoom.removeSocket(socket);
            socket.close();
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        } catch (NullPointerException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    @ResponseBody
    @RequestMapping("/get/allChatRooms.json")
    public List<ChatRoom> ChatRooms(@RequestParam("userId") Integer userId) {
        // 获取所有已加入的房间
        List<ChatRoom> chatRooms = chatRoomService.getChatRoomsByUserId(userId);
        for (ChatRoom chatRoom : chatRooms) {
            Integer roomId = chatRoom.getId();
            if (ChatRoomMap.containsChatRoom(roomId)) {    // 服务器已开启该房间
                chatRoom = ChatRoomMap.getChatRoom(roomId);
            } else {
                ChatRoomMap.addChatRoom(roomId, chatRoom);
                try {
                    chatRoom.open();    // 开启房间服务器
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                if (SocketMap.containsSocket(roomId, userId)) {    // 释放之前的连接
                    Socket socket = SocketMap.getSocket(roomId, userId);
                    SocketMap.removeSocket(roomId, userId);
                    SocketMap.removeObjectOutputStream(socket);
                    SocketMap.removeObjectInputStream(socket);
                    chatRoom.removeSocket(socket);
                    socket.close();
                }
                // 获取房间套接字
                ServerSocket server = chatRoom.getServer();
                // -----连接到房间-----
                Socket socket = new Socket(server.getInetAddress().getHostAddress(), server.getLocalPort());
                // 设置超时时间
                socket.setSoTimeout(timeout);
                // 保存socket
                SocketMap.addSocket(roomId, userId, socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return chatRooms;
    }
}
