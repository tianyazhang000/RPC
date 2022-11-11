package com.rpc;

import com.rpc.connection.ZkConnection;
import com.rpc.registry.RpcRegistry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Properties;

/**
 * @author tyz
 * @date 2022/11/8 14:45
 * 框架入口
 */
public class RpcFactory {

    //用于保存配置信息
    private static final Properties config = new Properties();
    //连接对象
    private static final ZkConnection connection;
    //注册器对象
    private static final RpcRegistry registry;
    //用户读取初始化的配置对象
    private static final Properties services = new Properties();

    /**
     * @author tyz
     * @date 2022/11/8 14:46
     * 固定逻辑，在classpath下，提供配置文件，命名为rpc.properties
     * 固定文件结构：
     *      registry.ip=服务端IP地址，默认为localhost
     *      registry.port=服务端端口号，默认为9090
     *      zk.server=Zookeeper访问地址，默认为localhost:2181
     *      zk.sessionTimeout=Zookeeper连接会话超时，默认为10000
     */

    static {
        try {
            //获取classpath类路径下的配置文件输入流
            InputStream input = RpcFactory.class.getClassLoader().getResourceAsStream("rpc.properties");
            //读取配置文件，初始化配置对象
            config.load(input);
            //获取服务端IP
            String serverIp = config.getProperty("registry.ip") == null ? "localhost" : config.getProperty("registry.ip");
            //获取服务端端口号
            int serverPort = config.getProperty("registry.port") == null ? 9090 : Integer.parseInt(config.getProperty("registry.port"));
            //获取zk服务器地址
            String zkServer = config.getProperty("zk.server") == null ? "localhost:2181" : config.getProperty("zk.server");
            //获取zk客户端会话超时时间
            int zkSessionTimeout = config.getProperty("zk.sessionTimeout") == null ? 10000 : Integer.parseInt(config.getProperty("zk.sessionTimeout"));
            connection = new ZkConnection(zkServer,zkSessionTimeout);
            registry = new RpcRegistry();
            registry.setIp(serverIp);
            registry.setPort(serverPort);
            registry.setConnection(connection);
            //创建一个RMI的注册器
            LocateRegistry.createRegistry(serverPort);
            //初始化zk中的父节点/rpc
            ZooKeeper conn = connection.getConnection();
            List<String> children = conn.getChildren("/", false);
            //不存在子节点
            if(!children.contains("tyz")){
                //创建节点/tyz
                connection.getConnection().create("/tyz",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            List<String> tyzChildren = connection.getConnection().getChildren("/tyz", false);
            //不存在子节点
            if(!tyzChildren.contains("rpc")){
                //创建节点/tyz
                connection.getConnection().create("/tyz/rpc",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //判断在classpath下，是否有一个配置文件，命名为rpc-services.properties
            //如果有这个配置，则自动初始化；没有就忽略后续逻辑
            //配置文件格式是 接口全命名=实现类全命名
            InputStream servicesInput = RpcFactory.class.getClassLoader().getResourceAsStream("rpc-services.properties");
            if(servicesInput != null){
                services.load(servicesInput);
                services.forEach((a,b) -> {
                    try {
                        Class<Remote> serviceInterface = (Class<Remote>) Class.forName(a.toString());
                        Remote serviceObject = (Remote) Class.forName(b.toString()).newInstance();
                        registry.registerService(serviceInterface,serviceObject);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (KeeperException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }
    //提供一个快速的注册服务和创建客户端代理对象的静态工具方法
    public static void registerService(Class<? extends Remote> serviceInterface, Remote serviceObject) throws IOException, InterruptedException, KeeperException {
        registry.registerService(serviceInterface,serviceObject);
    }
    public static <T extends Remote> T getServiceProxy(Class<T> serviceInterface) throws NotBoundException, IOException, InterruptedException, KeeperException {
        return registry.getServiceProxy(serviceInterface);
    }
}
