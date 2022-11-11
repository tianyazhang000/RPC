package com.tyz.rpc.consumer.client;

import com.tyz.rpc.consumer.handler.NettyRpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author tyz
 * @date 2022/11/10 20:13
 * 1、连接服务端
 * 2、关闭资源
 * 3、提供发送消息的方法
 */
@Component
public class NettyRpcClient implements InitializingBean, DisposableBean{

    EventLoopGroup group = null;
    Channel channel = null;
    @Autowired
    NettyRpcClientHandler nettyRpcClientHandler;

    ExecutorService service = Executors.newCachedThreadPool();

    /**
     * @author tyz
     * @date 2022/11/10 20:14
     * 1、连接服务端
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            //创建线程组
            group = new NioEventLoopGroup();
            //创建客户端启动助手
            Bootstrap bootstrap = new Bootstrap();
            //设置参数
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加编解码器
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            //添加自定义处理器
                            socketChannel.pipeline().addLast(nettyRpcClientHandler);
                        }
                    });
            //连接服务
            channel = bootstrap.connect("127.0.0.1", 8899).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            if(channel != null){
                channel.close();
            }
            if(group != null){
                group.shutdownGracefully();
            }
        }

    }

    @Override
    public void destroy() throws Exception {
        if(channel != null){
            channel.close();
        }
        if(group != null){
            group.shutdownGracefully();
        }
    }

    /**
     * @author tyz
     * @date 2022/11/10 20:23
     * 消息发送
     */
    public Object send(String msg) throws ExecutionException, InterruptedException {
        nettyRpcClientHandler.setReqMsg(msg);
        Future submit = service.submit(nettyRpcClientHandler);
        return submit.get();
    }

}
