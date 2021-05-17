package com.example.controller;

import com.example.entity.Room;
import com.example.entity.Message;
import com.example.entity.Server;
import com.example.entity.User;
import com.example.service.RoomService;
import com.example.service.MessageService;
import com.example.service.UserService;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import com.example.utils.SocketMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

@Controller
@RequestMapping("/room")
public class RoomController {
    @Autowired
    RoomService roomService;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    /**
     * 创建房间
     */
    @ResponseBody
    @RequestMapping("/create.json")
    public ResultEntity<Integer> createRoom(@RequestBody Room room) {
        // 创建房间
        roomService.addRoom(room);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, room.getId());
    }

    /**
     * 加入房间
     */
    @ResponseBody
    @RequestMapping("/join.json")
    public ResultEntity<Room> joinRoom(@RequestParam("userId") Integer userId,
                                       @RequestParam("roomId") Integer roomId,
                                       @RequestParam("password") String password) {
        if (roomService.judgeUserInRoom(roomId, userId)) {  // 已经在房间中
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_HAS_JOINED, null);
        }
        // 根据id获取房间
        Room room = roomService.getRoomById(roomId);
        room.setPassword(null);
        if (!room.getEnable() && !room.getHostId().equals(userId)) {    // 房间已关闭
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_DISABLE, null);
        }
        if (!roomService.addUserToRoom(roomId, userId, password)) { // 加入房间失败
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_NOT_FOUNT, null);
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
                oos.writeObject(new Message(null, roomId, user, null));
            }
            // 获取用户名
            String username = user.getUsername();
            // 提示信息
            String msg = username + " 已进入房间";
            Message message = new Message(msg, roomId, user, null);
            // 保存数据库
            messageService.addMessage(message);
            // 发送数据
            oos.writeObject(message);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, room);
        } catch (IOException | NullPointerException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    /**
     * 退出房间
     */
    @ResponseBody
    @RequestMapping("/do/exitRoom.json")
    public ResultEntity<String> doExitRoom(@RequestParam("roomId") Integer roomId,
                                           @RequestParam("userId") Integer userId) {
        // 从数据库移除
        if (!roomService.removeUserFromRoom(roomId, userId)) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_NOT_FOUNT, null);
        }

        try {
            // 获取用户名
            String username = userService.getUsernameById(userId);
            // 提示信息
            String msg = username + " 已退出房间";
            // 封装数据
            User user = userService.getUserById(userId);
            Message message = new Message(msg, roomId, user, null);
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

    /**
     * 关闭房间
     */
    @ResponseBody
    @RequestMapping("/do/closeRoom.json")
    public ResultEntity<String> doCloseRoom(@RequestParam("roomId") Integer roomId,
                                            @RequestParam("userId") Integer userId,
                                            @RequestParam("password") String password) {
        // 比对密码
        User user = userService.getUserById(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_PASSWORD_ERROR, null);
        }
        try {
            // 关闭房间
            Room room = roomService.getRoomById(roomId);
            if (!room.getEnable()) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_DISABLE, null);
            }
            room.setEnable(false);
            roomService.update(room);
            // 提示信息
            String msg = "房主关闭了房间";
            Message message = new Message(msg, roomId, user, null);
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

    /**
     * 开启房间
     */
    @ResponseBody
    @RequestMapping("/do/openRoom.json")
    public ResultEntity<String> doOpenRoom(@RequestParam("roomId") Integer roomId,
                                           @RequestParam("userId") Integer userId) {
        User user = userService.getUserById(userId);
        try {
            // 开启房间
            Room room = roomService.getRoomById(roomId);
            if (room.getEnable()) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_ENABLE, null);
            }
            room.setEnable(true);
            roomService.update(room);
            // 提示信息
            String msg = "房主开启了房间";
            Message message = new Message(msg, roomId, user, null);
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
    @RequestMapping("/get/allRooms.json")
    public List<Room> rooms(@RequestParam("userId") Integer userId) {
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
            oos.writeObject(new Message(null, null, new User(userId), null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取所有已加入的房间
        return roomService.getRoomsByUserId(userId);
    }

    @ResponseBody
    @RequestMapping("/update/unread.json")
    public ResultEntity<Object> updateUnread(@RequestParam("roomId") Integer roomId,
                                             @RequestParam("userId") Integer userId,
                                             @RequestParam("unread") Integer unread) {
        roomService.updateUnread(roomId, userId, unread);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }

    @ResponseBody
    @RequestMapping("/get/unread.json")
    public ResultEntity<Integer> getUnread(@RequestParam("roomId") Integer roomId,
                                           @RequestParam("userId") Integer userId) {
        Integer unread = roomService.getUnread(roomId, userId);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, unread);
    }
}
