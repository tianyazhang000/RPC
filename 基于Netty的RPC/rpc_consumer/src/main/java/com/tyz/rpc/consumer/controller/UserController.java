package com.tyz.rpc.consumer.controller;

import com.tyz.rpc.api.IUserService;
import com.tyz.rpc.consumer.anno.RpcReference;
import com.tyz.rpc.pojo.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tyz
 * @date 2022/11/10 18:30
 * 用户控制类
 */
//@RestController = @ResponseBody + @Controller
@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReference
    private IUserService userService;

    @RequestMapping("/getUserById")
    public User getUserById(int id){
        return userService.getById(id);
    }

}
