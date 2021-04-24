package com.example.controller;

import com.example.entity.Message;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import com.example.utils.SocketMap;
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
import java.util.UUID;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @ResponseBody
    @RequestMapping("/send/message.json")
    public ResultEntity<String> sendMessage(@RequestBody Message message, HttpSession session) {
        if (message.getMsg() == null || message.getMsg().isEmpty()) {// msg不能为空
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_STRING_INVALIDATE, null);
        }
        String sessionId = session.getId();// 获取用户标识
        Socket socket = SocketMap.getSocket(sessionId, message.getSourcePort());// 获取对应的socket
        if (socket == null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
        }
        try {
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 发送数据
            oos.writeObject(message);
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SYSTEM_ERROR_IO_EXCEPTION, null);
        }
    }

    /**
     * @param localPort 本地端口
     */
    @ResponseBody
    @RequestMapping("/recv/message.json")
    public ResultEntity<Message> recvMessage(@RequestParam("localPort") Integer localPort,
                                             HttpSession session) {
        String sessionId = session.getId();// 获取用户标识
        Socket socket = SocketMap.getSocket(sessionId, localPort);// 获取对应的socket
        if (socket == null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
        }
        try {
            // 获取输入流
            ObjectInputStream ois = SocketMap.getObjectInputStream(socket);
            // 获取Message对象
            Message message = (Message) ois.readObject();
            return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, message);
        } catch (IOException | ClassNotFoundException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
    }

    @ResponseBody
    @RequestMapping("/upload/file.json")
    public ResultEntity<String> uploadFile(@RequestParam(value = "files", required = false) MultipartFile file,
                                           @RequestParam(value = "localAddress", required = false) String localAddress,
                                           @RequestParam(value = "localPort", required = false) Integer localPort,
                                           HttpSession session) {
        try {
            // 获取用户标识
            String sessionId = session.getId();
            // 获取套接字
            Socket socket = SocketMap.getSocket(sessionId, localPort);
            if (socket == null) {
                return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_SOCKET_NOT_FOUND, null);
            }

            String path = session.getServletContext().getRealPath("upload") + '\\' + localAddress + "\\" + localPort;
            String fileName = file.getOriginalFilename();
            int p = fileName.lastIndexOf('.');
            String prefix = fileName.substring(0, p);// 文件名
            String suffix = fileName.substring(p + 1);// 文件后缀
            String filePath = path + '\\' + prefix + '-' + UUID.randomUUID().toString().replace("-", "") + '.' + suffix;// 文件路径
            File desFile = new File(filePath);
            if (!desFile.getParentFile().exists()) {// 如果文件夹不存在则创建
                desFile.mkdirs();
            }
            // 保存文件
            file.transferTo(desFile);
            // 提示信息
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
            String msg = localAddress + ":" + localPort + " 上传了文件: " + fileName + "(" + new DecimalFormat("#.00").format(size) + unit + ")";
            // 封装数据
            Message message = new Message(msg, localAddress, localPort, new File(filePath));
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 发送数据
            oos.writeObject(message);
        } catch (IllegalStateException | IOException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }

    @RequestMapping("/download/file.html")
    public void downloadFile(@RequestParam("filePath") String filePath,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        OutputStreamWriter osw = null;
        InputStream in = null;
        OutputStream os = null;
        try {
            request.setCharacterEncoding("utf-8");
            // 获取需要下载的文件名
            String fileName = filePath.substring(filePath.lastIndexOf('\\') + 1);
            fileName = fileName.substring(0, fileName.lastIndexOf('-')) + fileName.substring(fileName.lastIndexOf('.'));
            //下载文件需要设置消息头
            response.addHeader("content-Type", "application/octet-stream");//MIME类型:二进制文件(任意文件)
            response.addHeader("content-Disposition", "attachment;filename=" + UriUtils.encode(fileName, "utf-8"));//filename包含后缀

            osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
            File file = new File(filePath);
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
    public ResultEntity<String> sendImage(@RequestParam(value = "image", required = false) MultipartFile file,
                                          @RequestParam(value = "localAddress", required = false) String localAddress,
                                          @RequestParam(value = "localPort", required = false) Integer localPort,
                                          HttpSession session,
                                          HttpServletRequest request) {
        if (file.getSize() > 20 * 1024 * 1024) {// 图片大小不能大于20MB
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_IMAGE_SIZE_TOO_LARGE, null);
        }
        try {
            // 获取用户标识
            String sessionId = session.getId();
            // 获取套接字
            Socket socket = SocketMap.getSocket(sessionId, localPort);

            String path = session.getServletContext().getRealPath("upload") + '\\' + localAddress + "\\" + localPort;
            String fileName = file.getOriginalFilename();
            int p = fileName.lastIndexOf('.');
            String prefix = fileName.substring(0, p);// 文件名
            String suffix = fileName.substring(p + 1);// 文件后缀
            fileName = prefix + '-' + UUID.randomUUID().toString().replace("-", "") + '.' + suffix;
            String filePath = path + '\\' + fileName; // 文件绝对路径
            File desFile = new File(filePath);
            if (!desFile.getParentFile().exists()) {// 如果文件夹不存在则创建
                desFile.mkdirs();
            }
            // 保存文件
            file.transferTo(desFile);
            // 获取图片路径
            String ContextPath = request.getRequestURL().toString();
            ContextPath = ContextPath.substring(0, ContextPath.indexOf("chat"));
            ContextPath += "upload/" + localAddress + '/' + localPort + '/' + fileName;
            // 封装数据
            Message message = new Message(null, localAddress, localPort);
            message.setImage(filePath, ContextPath);
            // 获取输出流
            ObjectOutputStream oos = SocketMap.getObjectOutputStream(socket);
            // 发送数据
            oos.writeObject(message);
        } catch (IllegalStateException | IOException e) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, e.toString(), null);
        }
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }
}
