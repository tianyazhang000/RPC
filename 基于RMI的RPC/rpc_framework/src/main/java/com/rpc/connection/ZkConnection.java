package com.rpc.connection;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * zookeeper连接的自定义类,获取zk的连接对象
 * @author tyz
 * @date 2022/11/8 11:00
*/
public class ZkConnection {
    //保存zk地址。格式是ip：port
    private String zkServer;
    //保存会话时间
    private int sessionTimeout;

    public ZkConnection(){
        //默认值
        this.zkServer = "localhost:2181";
        this.sessionTimeout = 10000;
    }
    public ZkConnection(String zkServer, int sessionTimeout) {
        this.zkServer = zkServer;
        this.sessionTimeout = sessionTimeout;
    }

    public ZooKeeper getConnection() throws IOException {
        return new ZooKeeper(zkServer, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
            }
        });
    }

}
