package com.rpc.registry;

import com.rpc.connection.ZkConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.util.List;

/**
 * 注册器工具
 * 通过zk连接对象，和传入的Remote接口实现对象，完成RMI地址的拼接和保存（保存在zk中）
 * 缺少LocateRegistry对象。 缺少当前类型中属性的赋值过程。整体流程，缺少ZkConnection的创建过程。
 * @author tyz
 * @date
 */
public class RpcRegistry {
    //连接对象
    //    协议://ip:port/接口
    private ZkConnection connection;
    private String ip;
    private int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ZkConnection getConnection(){
        return connection;
    }

    public void setConnection(ZkConnection connection){
        this.connection = connection;
    }

    /**
     * 反复注册会抛出异常
     *
     * @author tyz
     * @date
     * 注册服务的方法
     * 1、拼接RMI的访问地址URI
     * 2、把访问地址URI存储在Zookeeper中。
     * @param serviceInterface 服务接口类对象。 接口必须是Remote接口
     * @param serviceObject 服务实现类型的对象。
     * @throws IOException, InterruptedException, KeeperException 抛出异常代表注册失败
     */
    public void registerService(Class<? extends Remote> serviceInterface,Remote serviceObject) throws IOException, InterruptedException, KeeperException {
        //拼接地址
        String rmi = "rmi://" + ip + ":" + port + "/" + serviceInterface.getName();
        String path = "/tyz/rpc/" + serviceInterface.getName();

        List<String> children = connection.getConnection().getChildren("/tyz/rpc", false);
        if(children.contains(serviceInterface.getName())){
            Stat stat = new Stat();
            connection.getConnection().getData(path,false,stat);
            connection.getConnection().delete(path,stat.getCversion());
        }
        //将地址存储到Zookeeper中,获取一个连接对象
        connection.getConnection().create(path,rmi.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //把服务对象，在RMI的registry中注册
        Naming.rebind(rmi,serviceObject);
    }
    /**
     * @author tyz
     * @date
     * 根据服务接口类型，访问zk，获取RMI的远程代理对象
     * 1、拼接一个zk中的节点名称
     * 2、访问zk，查询节点中存储的数据
     * 3、根据查询到结果，创建一个代理对象
     */
    public <T extends Remote> T getServiceProxy(Class<T> serviceInterface) throws IOException, InterruptedException, KeeperException, NotBoundException {
        //拼接一个zk中的节点名称
        String path = "/tyz/rpc/" + serviceInterface.getName();
        //访问zk，查询节点中存储的数据
        byte[] datas = connection.getConnection().getData(path, false, null);
        //把查询到字节数组，翻译成RMI的访问地址
        String rmi = new String(datas);
        //根据查询到结果，创建一个代理对象
        Object obj = Naming.lookup(rmi);
        return (T) obj;
    }
}
