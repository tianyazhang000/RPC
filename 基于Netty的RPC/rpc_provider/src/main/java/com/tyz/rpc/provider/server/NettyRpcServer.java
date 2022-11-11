package com.tyz.rpc.provider.server;

import com.tyz.rpc.provider.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tyz
 * @date 2022/11/10 18:37
 * Netty的服务端
 * 启动服务端监听接口
 */
@Component
public class NettyRpcServer implements DisposableBean {

    @Autowired
    NettyServerHandler nettyServerHandler;
    NioEventLoopGroup bossGroup = null;
    NioEventLoopGroup workerGroup = null;

        //用于启动Netty服务端
    public void start(String host,int port){
        try {
            //1、创建bossGroup和workerGroup
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            //2、设置启动助手
            ServerBootstrap bootstrap = new ServerBootstrap();
            //3、设置自动参数
            bootstrap.group(bossGroup,workerGroup).
                    channel(NioServerSocketChannel.class).
                    childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加String的编解码器
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            //添加自定义处理器
                            //todo
                            socketChannel.pipeline().addLast(nettyServerHandler);
                        }
                    });
            //绑定ip和端口号
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            System.out.println("Netty服务端启动成功");
            //监听通道的关闭状态
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //关闭资源
            if(bossGroup != null){
                bossGroup.shutdownGracefully();
            }
            if(workerGroup != null){
                workerGroup.shutdownGracefully();
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        //关闭资源
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }
}
