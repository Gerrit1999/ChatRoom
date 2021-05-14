package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@Controller
@SessionAttributes("sessionInfo")
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping("/do/register.json")
    public ResultEntity<String> doRegister(@RequestBody User user) {
        User findUser = userService.findUserByUsername(user.getUsername());
        if (findUser != null) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, CustomConstant.MESSAGE_USERNAME_EXIST, null);
        }
        if (!userService.addUser(user)) {
            return ResultEntity.createResultEntity(ResultEntity.ResultType.FAILED, "注册失败!", null);
        }
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }

    @RequestMapping("/login/failed.html")
    public String loginFailed() {
        return "redirect:../../index.jsp";
    }

    @ResponseBody
    @RequestMapping("/update/recentActiveTime.json")
    public ResultEntity<Object> updateRecentActiveTime(@RequestParam("userId") Integer userId) {
        userService.updateRecentActiveTime(userId);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, null);
    }


    @ResponseBody
    @RequestMapping("/get/activeList.json")
    public ResultEntity<List<User>> getActiveList(@RequestParam("roomId") Integer roomId) {
        int activeTime = 10;   // 60s内在线
        List<User> users = userService.getUsersByRoomIdActive(roomId, activeTime);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, users);
    }

    @ResponseBody
    @RequestMapping("/get/notActiveList.json")
    public ResultEntity<List<User>> getNotActiveList(@RequestParam("roomId") Integer roomId) {
        int activeTime = 10;   // 60s内在线
        List<User> users = userService.getUsersByRoomIdNotActive(roomId, activeTime);
        return ResultEntity.createResultEntity(ResultEntity.ResultType.SUCCESS, null, users);
    }
}
