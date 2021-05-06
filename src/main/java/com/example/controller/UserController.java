package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.CustomConstant;
import com.example.utils.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
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
}
