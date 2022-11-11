package com.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author tyz
 * @date 2022/11/8 16:07
 * 定义一个服务接口
 */
public interface UserService extends Remote {
    /**
     * @author tyz
     * @date 2022/11/8 16:08
     * 根据用户名查询用户,返回一个JSON格式1的字符串用于描述用户
     */
    String getUser(String name) throws RemoteException;
}
