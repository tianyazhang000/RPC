package com.user.service;

import com.pojo.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserService extends Remote {
    List<User> getUsersByName(String name) throws RemoteException;
}
