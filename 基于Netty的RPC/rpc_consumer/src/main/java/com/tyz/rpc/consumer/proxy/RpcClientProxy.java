package com.tyz.rpc.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.tyz.rpc.common.RpcRequest;
import com.tyz.rpc.common.RpcResponse;
import com.tyz.rpc.consumer.client.NettyRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author tyz
 * @date 2022/11/10 20:50
 * 客户端代理
 */
@Component
public class RpcClientProxy {

    @Autowired
    NettyRpcClient nettyRpcClient;

    Map<Class,Object> SERVICE_PROXY = new HashMap<>();

    /**
     * @author tyz
     * @date 2022/11/10 20:50
     * 获取代理对象
     */
    public Object getProxy(Class serviceClass){
        Object proxy = SERVICE_PROXY.get(serviceClass);
        if(proxy == null){
            //创建代理对象
            proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{serviceClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    //封装请求对象
                    RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setClassName(method.getDeclaringClass().getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setParameterTypes(method.getParameterTypes());
                    rpcRequest.setParameters(args);
                    try {
                        //发送消息
                        Object msg = nettyRpcClient.send(JSON.toJSONString(rpcRequest));
                        //将消息转化
                        RpcResponse rpcResponse = JSON.parseObject(msg.toString(), RpcResponse.class);
                        if(rpcResponse.getError() != null){
                            throw new RuntimeException(rpcResponse.getError());
                        }
                        if(rpcResponse.getResult() != null){
                            return JSON.parseObject(rpcResponse.getResult().toString(),method.getReturnType());
                        }
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
            });
        SERVICE_PROXY.put(serviceClass,proxy);
        return proxy;
        } else {
            return proxy;
        }
    }
}
