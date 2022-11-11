package com.tyz.rpc.consumer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * @author tyz
 * @date 2022/11/10 20:26
 * 客户端业务处理类
 */
@Component
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<String> implements Callable {
    ChannelHandlerContext context;
    //发送消息
    private String reqMsg;
    //接收消息
    private String respMsg;

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    /**
     * @author tyz
     * @date 2022/11/10 20:27
     * 读取服务端消息
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        respMsg = s;
        //唤醒等待线程
        notify();
    }

    /**
     * @author tyz
     * @date 2022/11/10 20:28
     * 通道连接就绪事件
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    /**
     * @author tyz
     * @date 2022/11/10 20:30
     * 给服务端发送消息
     */
    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(reqMsg);
        //将线程处于等待状态
        wait();
        return respMsg;
    }
}
