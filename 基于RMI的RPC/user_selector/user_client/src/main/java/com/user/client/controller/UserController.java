package com.user.client.controller;

import com.pojo.User;
import com.rpc.RpcFactory;
import com.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    private UserService userService;

    public UserController(){
        try {
            this.userService = RpcFactory.getServiceProxy(UserService.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("服务" + UserService.class.getName() + "不存在");
        }
    }

    @RequestMapping("/getUsersByName")
    @ResponseBody
    public List<User> getUsersByName(String name){
        try {
            List<User> users = userService.getUsersByName(name);
            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
