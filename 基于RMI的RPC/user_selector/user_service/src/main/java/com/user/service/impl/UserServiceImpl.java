package com.user.service.impl;

import com.pojo.User;
import com.rpc.RpcFactory;
import com.user.service.UserService;
import com.user.mapper.UserMapper;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

@Service
public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    public UserServiceImpl() throws RemoteException{
        try {
            RpcFactory.registerService(UserService.class,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
        System.out.println("注册服务：UserServiceImpl");
    }

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getUsersByName(String name) throws RemoteException {
        return userMapper.selectByName(name);
    }
}
