package com.rpc;

import com.service.UserService;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.rmi.NotBoundException;

/**
 * @author tyz
 * @date 2022/11/9 10:54
 * 测试基于自定义RPC框架的客户端开发
 */
public class TestRpcClient {
    public static void main(String[] args) {
        //通过自定义框架链接zk，获取接口的动态代理对象
        try {
            UserService userService = RpcFactory.getServiceProxy(UserService.class);
            System.out.println(userService.getClass().getName());
            String result = userService.getUser("管理员");
            System.out.println("远程服务返回查询结果:" + result);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }
}
