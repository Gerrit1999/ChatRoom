package com.example.listener;

import com.example.entity.ChatRoom;
import com.example.utils.ChatRoomMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * 服务器关闭后的资源回收操作
 */
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // 销毁房间线程
        Map<Integer, ChatRoom> map = ChatRoomMap.getMap();
        for (Map.Entry<Integer, ChatRoom> entry : map.entrySet()) {
            entry.getValue().close();
        }
    }
}
