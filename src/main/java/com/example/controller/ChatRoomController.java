package com.example.controller;

import com.example.entity.ChatRoom;
import com.example.entity.Message;
import com.example.entity.Server;
import com.example.entity.User;
import com.example.service.ChatRoomService;
import com.example.service.MessageService;
import com.example.service.UserService;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import com.example.utils.SocketMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @Autowired
    MessageService messageService;

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
        if (chatRoomService.judgeUserInRoom(roomId, userId)) { // 已经在房间中
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_HAS_JOINED, null);
        }
        if (!chatRoomService.addUserToChatRoom(roomId, userId, password)) { // 加入房间失败
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }

        try {
            // 根据id获取user
            User user = userService.getUserById(userId);
            // 连接到server socket
            Socket socket = SocketMap.getConnectSocket(userId);
            ObjectOutputStream oos;
            if (socket != null) {
                oos = SocketMap.getObjectOutputStream(socket);
            } else {
                // 获取socket
                socket = new Socket(Server.hostAddress, Server.localPort);
                // 设置超时时间
                socket.setSoTimeout(CustomConstant.acceptTimeOut);
                // 保存socket
                SocketMap.addConnectSocket(userId, socket);
                // 获取输出流
                oos = SocketMap.getObjectOutputStream(socket);
                // 连接到server
                oos.writeObject(new Message(null, roomId, user));
            }
            // 获取用户名
            String username = user.getUsername();
            // 提示信息
            String msg = username + " 已进入房间";
            Message message = new Message(msg, roomId, user);
            // 保存数据库
            messageService.addMessage(message);
            // 发送数据
            oos.writeObject(message);
            // 根据id获取房间
            ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);
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
        // 从数据库移除
        if (!chatRoomService.removeUserFromChatRoom(roomId, userId)) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }

        try {
            // 获取用户名
            String username = userService.getUsernameById(userId);
            // 提示信息
            String msg = username + " 已退出房间";
            // 封装数据
            User user = userService.getUserById(userId);
            Message message = new Message(msg, roomId, user);
            // 保存数据库
            messageService.addMessage(message);
            // 获取socket
            Socket socket = SocketMap.getConnectSocket(userId);
            if (socket == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
            }
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 发送数据
            oos.writeObject(message);
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
        // 重新连接到server
        try {
            // 删除之前的socket
            SocketMap.removeConnectSocket(userId);
            SocketMap.removeAcceptSocket(userId);
            // 获取socket
            Socket socket = new Socket(Server.hostAddress, Server.localPort);
            // 设置超时时间
            socket.setSoTimeout(CustomConstant.acceptTimeOut);
            // 保存socket
            SocketMap.addConnectSocket(userId, socket);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 连接到server
            oos.writeObject(new Message(null, null, new User(userId)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取所有已加入的房间
        return chatRoomService.getChatRoomsByUserId(userId);
    }

    @ResponseBody
    @RequestMapping("/update/unread.json")
    public ResultEntity<Object> updateUnread(@RequestParam("roomId") Integer roomId,
                                             @RequestParam("userId") Integer userId,
                                             @RequestParam("unread") Integer unread) {
        chatRoomService.updateUnread(roomId, userId, unread);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }

    @ResponseBody
    @RequestMapping("/get/unread.json")
    public ResultEntity<Integer> getUnread(@RequestParam("roomId") Integer roomId,
                                           @RequestParam("userId") Integer userId) {
        Integer unread = chatRoomService.getUnread(roomId, userId);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, unread);
    }
}
