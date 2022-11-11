package com.tyz.rpc.provider.handler;

import com.alibaba.fastjson.JSON;
import com.tyz.rpc.common.RpcRequest;
import com.tyz.rpc.common.RpcResponse;
import com.tyz.rpc.provider.anno.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tyz
 * @date 2022/11/10 19:29
 * 自定义业务处理类
 * 1、将标有@RpcService的注解的Bean进行缓存
 * 2、接收客户端的请求
 * 3、根据传递过来的BeanName从缓存中查找
 * 4、通过反射调用bean方法
 * 5、给客户端响应
 */
@Component
@ChannelHandler.Sharable //设置通道共享
public class NettyServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {

    static Map<String,Object> SERVICE_INSTANCE_MAP = new HashMap<>();

    /**
     * @author tyz
     * @date 2022/11/10 19:35
     * 1、将标有@RpcService的注解的Bean进行缓存
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //通过注解获取bean集合
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        //循环遍历
        serviceMap.forEach((key,value) -> {
            if(value.getClass().getInterfaces().length == 0){
                throw new RuntimeException("对外暴露的服务必须实现接口");
            }
            //默认处理第一个作为缓存bean的名字
            String serviceName = value.getClass().getInterfaces()[0].getName();
            SERVICE_INSTANCE_MAP.put(serviceName,value);
            System.out.println(SERVICE_INSTANCE_MAP);
        });
    }

    /**
     * @author tyz
     * @date 2022/11/10 19:33
     * 通道读取就绪事件--读取客户端消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        //2、接收客户端的请求
        RpcRequest rpcRequest = JSON.parseObject(s, RpcRequest.class);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());

        //业务处理
        try {
            rpcResponse.setResult(handler(rpcRequest));
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setError(e.getMessage());
        }
        //5、给客户端响应
        channelHandlerContext.writeAndFlush(JSON.toJSONString(rpcResponse));
    }

    private Object handler(RpcRequest rpcRequest) throws InvocationTargetException {
//         3、根据传递过来的BeanName从缓存中查找
        Object serviceBean = SERVICE_INSTANCE_MAP.get(rpcRequest.getClassName());
        if(serviceBean == null){
            throw new RuntimeException("服务端没有找到服务");
        }
//         4、通过反射调用bean方法
        FastClass proxyClass = FastClass.create(serviceBean.getClass());
        FastMethod method = proxyClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        return method.invoke(serviceBean, rpcRequest.getParameters());
    }

}
