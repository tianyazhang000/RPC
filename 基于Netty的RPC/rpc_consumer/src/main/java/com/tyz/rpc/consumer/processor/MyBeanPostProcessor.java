package com.tyz.rpc.consumer.processor;

import com.tyz.rpc.consumer.anno.RpcReference;
import com.tyz.rpc.consumer.proxy.RpcClientProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    RpcClientProxy rpcClientProxy;

    /**
     * @author tyz
     * @date 2022/11/10 21:13\
     * 自定义注解的注入
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //查看bean的字段中有没有对应的注解
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for(Field field : declaredFields){
            //循环查看字段中是否包含注解
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if(annotation != null){
                //获取代理对象
                Object proxy = rpcClientProxy.getProxy(field.getType());
                try {
                    //属性的注入
                    field.setAccessible(true);
                    field.set(bean,proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
