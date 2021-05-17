package com.example.listener;

import com.example.entity.Server;
import com.example.utils.SocketMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 * 服务器关闭后的资源回收操作
 */
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 开启server socket
        try {
            Server.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // 关闭所有socket
        try {
            SocketMap.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
