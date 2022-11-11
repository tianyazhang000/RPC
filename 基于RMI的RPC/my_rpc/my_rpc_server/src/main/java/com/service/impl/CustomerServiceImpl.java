package com.service.impl;

import com.service.CustomerService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class CustomerServiceImpl extends UnicastRemoteObject implements CustomerService {

    public CustomerServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String getCustomer(String name) throws RemoteException {
        System.out.println("查询客户" + name);
        return "查询客户" + name;
    }

    @Override
    public int addCustomer(String name) throws RemoteException {
        System.out.println("新增客户："+ name);
        return 1;
    }
}
