package com.example.controller;

import com.example.entity.ChatRoom;
import com.example.entity.Message;
import com.example.utils.ChatRoomMap;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import com.example.utils.SocketMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

@Controller
@RequestMapping("/chatRoom")
public class ChatRoomController {
    private String hostAddress = null;

    public ChatRoomController() {
        // 获取本机ip
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建房间
     */
    @ResponseBody
    @RequestMapping("/create/chatRoom.json")
    public ResultEntity<Integer> createChatRoom(HttpSession session) {
        try {
            ServerSocket socket = new ServerSocket();
            // 绑定ip
            socket.bind(new InetSocketAddress(hostAddress, 0));
            // 获取端口号
            int port = socket.getLocalPort();
            // 开启服务器
            ChatRoom chatRoom = new ChatRoom(socket);
            chatRoom.open();
            ChatRoomMap.addChatRoom(port, chatRoom);
            // 存入session域
            session.setAttribute("port", port);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, port);
        } catch (IOException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    /**
     * 加入房间
     *
     * @param port 房间端口号
     */
    @ResponseBody
    @RequestMapping("/join/chatRoom.json")
    public ResultEntity<String> toChatRoom(@RequestParam("port") Integer port, HttpSession session) {
        // 获取房间
        ChatRoom chatRoom = ChatRoomMap.getChatRoom(port);
        if (chatRoom == null) {// 房间不存在
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }
        // 用户标识
        String sessionId = session.getId();
        try {
            // 连接到房间
            Socket socket = new Socket(hostAddress, port);
            socket.setSoTimeout(20 * 1000);// 设置超时时间
            // 获取ip和端口号
            String localAddress = socket.getLocalAddress().toString().substring(1);
            int localPort = socket.getLocalPort();
            // 保存到session
            session.setAttribute("localAddress", localAddress);
            session.setAttribute("port", port);
            session.setAttribute("localPort", localPort);
            // 保存socket
            SocketMap.addSocket(sessionId, localPort, socket);
            // 提示信息
            String msg = localAddress + ":" + localPort + " 已进入房间";
            // 封装数据
            Message message = new Message(msg, localAddress, localPort);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            if (oos == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION + "ObjectOutputStream", null);
            }
            // 发送数据
            oos.writeObject(message);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    /**
     * 退出房间
     *
     * @param port      房间端口号
     * @param localPort 本地端口号
     */
    @ResponseBody
    @RequestMapping("/do/exit.json")
    public ResultEntity<String> doExit(@RequestParam("port") Integer port,
                                       @RequestParam("localAddress") String localAddress,
                                       @RequestParam("localPort") Integer localPort,
                                       HttpSession session) {
        // 获取房间
        ChatRoom chatRoom = ChatRoomMap.getChatRoom(port);
        if (chatRoom == null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_CHAT_ROOM_NOT_FOUNT, null);
        }
        try {
            // 获取用户标识
            String sessionId = session.getId();
            // 获取套接字
            Socket socket = SocketMap.getSocket(sessionId, localPort);
            // 提示信息
            String msg = localAddress + ":" + localPort + " 已离开房间";
            // 封装数据
            Message message = new Message(msg, localAddress, localPort);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            if (oos == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION + "ObjectOutputStream", null);
            }
            // 发送数据
            oos.writeObject(message);
            if (socket == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
            }
            // 从房间移除
            chatRoom.removeSocket(socket);
            // 从map中移除
            SocketMap.removeSocket(sessionId, localPort);
            // 清除session
            session.removeAttribute("port");
            session.removeAttribute("localAddress");
            session.removeAttribute("localPort");
        } catch (IOException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }
}
