package com.example.controller;

import com.example.entity.Image;
import com.example.entity.Message;
import com.example.entity.Room;
import com.example.entity.User;
import com.example.service.*;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import com.example.utils.SocketMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    @Autowired
    MessageService messageService;

    @Autowired
    FileService fileService;

    @Autowired
    ImageService imageService;

    private static final int intervalDays = 7;

    @ResponseBody
    @RequestMapping("/send/message.json")
    public ResultEntity<String> sendMessage(@RequestBody Message message, HttpSession session) {
        if (message.getMessage() == null || message.getMessage().isEmpty()) {// msg不能为空
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_STRING_INVALIDATE, null);
        }
        Room room = roomService.getRoomById(message.getRoomId());
        if (!room.getEnable()) {  // 房间已关闭
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_DISABLE, null);
        }
        int n = message.getMessage().length();
        if (n > 256) {
            // 消息过长, 转为txt发送
            Integer senderId = message.getSender().getId();
            Integer roomId = message.getRoomId();
            String fileName = UUID.randomUUID().toString().replace("-", "") + ".txt";
            String filePath = session.getServletContext().getRealPath("upload") + "\\room_" + roomId + "\\user_" + senderId + '\\' + fileName;
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                if (!file.mkdirs()) {
                    return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_MKDIRS, null);
                }
            }
            //写入文件
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(filePath, true);
                fileWriter.write(message.getMessage());
            } catch (IOException e) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 文件大小
            String unit;
            double size = file.length();

            if (size >= 1024 * 1024) {
                unit = "MB";
                size /= 1024 * 1024;
            } else if (size >= 1024) {
                unit = "KB";
                size /= 1024;
            } else {
                unit = "B";
            }
            String msg = message.getSender().getUsername() + " 上传了文件: " + fileName + "(" + new DecimalFormat("#.00").format(size) + unit + ")";

            // 修改message对象
            com.example.entity.File myFile = new com.example.entity.File();
            myFile.setName(fileName);
            myFile.setPath(filePath);
            myFile.setSize(file.length());
            message.setMessage(msg);
            message.setFile(myFile);
        }
        // 保存数据库
        messageService.addMessage(message);
        Integer userId = message.getSender().getId();
        Socket socket = SocketMap.getConnectSocket(userId); // 获取对应的socket
        if (socket == null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
        }

        try {
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 发送数据
            oos.writeObject(message);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_IO_EXCEPTION, null);
        }
    }

    @ResponseBody
    @RequestMapping("/recv/message.json")
    public ResultEntity<Message> recvMessage(@RequestParam("userId") Integer userId) {
        Socket socket = SocketMap.getConnectSocket(userId);    // 获取对应的socket
        if (socket == null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
        }
        try {
            // 获取输入流
            ObjectInputStream ois = SocketMap.getObjectInputStream(socket);
            // 获取Message对象
            Message message = null;
            try {
                message = (Message) ois.readObject();// socket的超时时间已经设置为20s
            } catch (IOException ignored) {
            }
            if (message != null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, message);
            } else {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_RECEIVE_TIMEOUT, null);
            }
        } catch (ClassNotFoundException | IOException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    @ResponseBody
    @RequestMapping("/upload/file.json")
    public ResultEntity<String> uploadFile(@RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile,
                                           @RequestParam("roomId") Integer roomId,
                                           @RequestParam("senderId") Integer senderId,
                                           @RequestParam("receiverId") Integer receiverId,
                                           HttpSession session) {
        try {
            Room room = roomService.getRoomById(roomId);
            if (!room.getEnable()) {  // 房间已关闭
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_DISABLE, null);
            }
            // 获取套接字
            Socket socket = SocketMap.getConnectSocket(senderId);
            if (socket == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
            }
            // 保存文件
            com.example.entity.File file = saveFile(roomId, senderId, multipartFile, session);
            // -----提示信息-----

            // 文件大小
            String unit;
            double size = file.getSize();

            if (size >= 1024 * 1024) {
                unit = "MB";
                size /= 1024 * 1024;
            } else if (size >= 1024) {
                unit = "KB";
                size /= 1024;
            } else {
                unit = "B";
            }

            // 获取用户名
            String username = userService.getUsernameById(senderId);
            String msg = username + " 上传了文件: " + file.getName() + "(" + new DecimalFormat("#.00").format(size) + unit + ")";
            // 封装数据
            User sender = userService.getUserById(senderId);
            User receiver = userService.getUserById(receiverId);
            Message message = new Message(msg, roomId, sender, receiver);
            message.setFile(file);
            // 保存数据库
            messageService.addMessage(message);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 发送数据
            oos.writeObject(message);
        } catch (Exception e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }

    @RequestMapping("/download/file.html")
    public void downloadFile(@RequestParam("path") String path,
                             @RequestParam("fileName") String fileName,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        OutputStreamWriter osw = null;
        InputStream in = null;
        OutputStream os = null;
        try {
            request.setCharacterEncoding("utf-8");
            // 下载文件需要设置消息头
            response.addHeader("content-Type", "application/octet-stream"); // MIME类型:二进制文件(任意文件)
            response.addHeader("content-Disposition", "attachment;filename=" + UriUtils.encode(fileName, "utf-8"));//filename包含后缀

            osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);

            File file = new File(path);
            // 获取要下载的文件输入流
            in = new FileInputStream(file);
            // 如果不填setContentLength，不会报错，但是下载的时候会显示大小未知
            response.setContentLength((int) file.length());

            int len;
            // 创建数据缓冲区
            byte[] buffer = new byte[1024];
            // 通过response对象获取OutputStream流
            os = response.getOutputStream();
            // 将FileInputStream流写入到buffer缓冲区
            while ((len = in.read(buffer)) > 0) {
                // 将缓冲区的数据输出到客户端浏览器
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
                if (in != null) {
                    in.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @ResponseBody
    @RequestMapping("/send/image.json")
    public ResultEntity<String> sendImage(@RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                          @RequestParam("roomId") Integer roomId,
                                          @RequestParam("senderId") Integer senderId,
                                          @RequestParam("receiverId") Integer receiverId,
                                          HttpSession session,
                                          HttpServletRequest request) {
        Room room = roomService.getRoomById(roomId);
        if (!room.getEnable()) {  // 房间已关闭
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_ROOM_DISABLE, null);
        }
        if (multipartFile.getSize() > 20 * 1024 * 1024) {// 图片大小不能大于20MB
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_IMAGE_SIZE_TOO_LARGE, null);
        }
        try {
            // 获取套接字
            Socket socket = SocketMap.getConnectSocket(senderId);
            if (socket == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
            }
            // 保存文件
            com.example.entity.File file = saveFile(roomId, senderId, multipartFile, session);
            Image image = saveImage(roomId, senderId, request, file);
            file.setImage(image);
            // 封装数据
            User sender = userService.getUserById(senderId);
            User receiver = userService.getUserById(receiverId);
            Message message = new Message(null, roomId, sender, receiver);
            message.setFile(file);
            // 保存数据库
            messageService.addMessage(message);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            if (oos == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION + "ObjectOutputStream", null);
            }
            // 发送数据
            oos.writeObject(message);
        } catch (IllegalStateException | IOException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }

    @ResponseBody
    @RequestMapping("/get/history/message.json")
    public ResultEntity<List<Message>> getHistoryMessage(@RequestParam("roomId") Integer roomId,
                                                         @RequestParam("userId") Integer userId) {
        // 获取最近7天的数据
        List<Message> messages = messageService.getIntervalMessage(roomId, userId, intervalDays);
        for (Message message : messages) {
            message.setRoomId(roomId);
        }
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, messages);
    }

    private com.example.entity.File saveFile(Integer roomId, Integer userId, MultipartFile multipartFile, HttpSession session) throws IOException {
        String basePath = session.getServletContext().getRealPath("upload") + "\\room_" + roomId + "\\user_" + userId;
        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null) {
            throw new RuntimeException(CustomConstant.MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION);
        }
        int p = fileName.lastIndexOf('.');
        String prefix = fileName.substring(0, p);// 文件名
        String suffix = fileName.substring(p + 1);// 文件后缀
        String filePath = basePath + '\\' + prefix + '-' + UUID.randomUUID().toString().replace("-", "") + '.' + suffix;// 文件路径

        File desFile = new File(filePath);
        if (!desFile.getParentFile().exists()) {// 如果文件夹不存在则创建
            if (!desFile.mkdirs()) {
                throw new RuntimeException(CustomConstant.MESSAGE_SYSTEM_ERROR_MKDIRS);
            }
        }
        // 保存文件
        multipartFile.transferTo(desFile);

        // 文件大小
        long size = multipartFile.getSize();
        // 封装到file
        return new com.example.entity.File(fileName, filePath, size);
    }

    private Image saveImage(Integer roomId, Integer userId, HttpServletRequest request, com.example.entity.File file) throws IOException {
        // 获取图片路径
        String url = request.getRequestURL().toString();
        url = url.substring(0, url.indexOf("chat"));
        String fileName = file.getPath().substring(file.getPath().lastIndexOf('\\') + 1);
        url += "upload/room_" + roomId + "/user_" + userId + '/' + fileName;
        // 封装到file对象
        return new Image(file.getPath(), url);
    }
}
