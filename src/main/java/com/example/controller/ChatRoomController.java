package com.example.controller;

import com.example.entity.ChatRoom;
import com.example.entity.Message;
import com.example.entity.Server;
import com.example.service.ChatRoomService;
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

    /**
     * 创建房间
     */
    @ResponseBody
    @RequestMapping("/create/chatRoom.json")
    public ResultEntity<Integer> createChatRoom(@RequestBody ChatRoom chatRoom) {
        try {
            // 开启服务器
            Class.forName("com.example.entity.Server");
            // 创建房间
            chatRoomService.addChatRoom(chatRoom);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, chatRoom.getId());
        } catch (ClassNotFoundException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    /**
     * 加入房间
     */
    @ResponseBody
    @RequestMapping("/join/chatRoom.json")
    public ResultEntity<ChatRoom> toChatRoom(@RequestParam("userId") Integer userId,
                                             @RequestParam("roomId") Integer roomId,
                                             @RequestParam("password") String password) {
        // 获取房间
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);
        if (chatRoom == null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }
        // 如果不在房间就加入房间
        if (!chatRoomService.judgeUserInRoom(roomId, userId) && !chatRoomService.addUserToChatRoom(roomId, userId, password)) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }
        try {
            // 连接到房间
            Socket socket = SocketMap.getSocket(roomId, userId);
            if (socket == null) {
                socket = new Socket(Server.getAddress(), Server.getPort());
            }
            // 设置超时时间
            socket.setSoTimeout(20 * 1000);
            // 保存socket
            SocketMap.addSocket(roomId, userId, socket);
            // 获取用户名
            String username = userService.getUsernameById(userId);
            // 提示信息
            String msg = username + " 已进入房间";
            // 封装数据
            Message message = new Message(msg, roomId, userId);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            if (oos == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION + "ObjectOutputStream", null);
            }
            // 发送数据
            oos.writeObject(message);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, chatRoom);
        } catch (IOException e) {
            e.printStackTrace();
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
        try {
            // 获取用户名
            String username = userService.getUsernameById(userId);
            // 提示信息
            String msg = username + " 已退出房间";
            // 封装数据
            Message message = new Message(msg, roomId, userId);
            // 获取socket
            Socket socket = SocketMap.getSocket(roomId, userId);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            if (oos == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION + "ObjectOutputStream", null);
            }
            // 发送数据
            oos.writeObject(message);
            // 删除socket
            SocketMap.removeSocket(roomId, userId);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    @ResponseBody
    @RequestMapping("/get/allChatRooms.json")
    public List<ChatRoom> getAllChatRooms(@RequestParam("userId") Integer userId) {
        return chatRoomService.getChatRoomsByUserId(userId);
    }
}
