package com.service.impl;

import com.service.UserService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author tyz
 * @date 2022/11/8 16:13
 * 用户服务实现类
 */
public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    public UserServiceImpl() throws RemoteException {
    }
    @Override
    public String getUser(String name) throws RemoteException {
        System.out.println("要查询的用户是：" + name);
        return "{\"name\":\"" + name + "\",\"age\":\"20\",\"gender\",\"男\"}";
    }
}
