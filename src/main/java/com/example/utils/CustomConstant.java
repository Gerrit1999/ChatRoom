package com.example.utils;

public class CustomConstant {
    public static final String MESSAGE_STRING_INVALIDATE = "字符串不合法! 请不要传入空字符串!";

    public static final String MESSAGE_SYSTEM_ERROR_IO_EXCEPTION = "系统错误: IO异常!";

    public static final String MESSAGE_SYSTEM_ERROR_NULL_POINTER_EXCEPTION = "系统错误! 空指针异常: ";

    public static final String MESSAGE_SYSTEM_ERROR_MKDIRS = "系统错误! 创建文件夹失败!";

    public static final String MESSAGE_ROOM_NOT_FOUNT = "抱歉, 房间不存在或密码错误!";

    public static final String MESSAGE_ROOM_DISABLE = "房间已关闭!";

    public static final String MESSAGE_ROOM_ENABLE = "房间已开启!";

    public static final String MESSAGE_ROOM_HAS_JOINED = "您已在房间中!";

    public static final String MESSAGE_SOCKET_NOT_FOUND = "系统错误! 找不到套接字!";

    public static final String MESSAGE_IMAGE_SIZE_TOO_LARGE = "图片大小不能大于20MB!";

    public static final String MESSAGE_RECEIVE_TIMEOUT = "接收数据超时!";

    public static final String MESSAGE_USERNAME_EXIST = "抱歉! 用户名已存在!";

    public static final String MESSAGE_PASSWORD_ERROR = "密码错误, 请重试!";

    public static final String MESSAGE_LOGIN_FAILED = "登录失败! 请检查用户名和密码是否正确!";

    public static final Integer activeTime = 10;    // 用户10s内在线表示活跃

    public static final Integer acceptTimeOut = 20 * 1000;    // 用户接收消息的超时时间为20s
}
