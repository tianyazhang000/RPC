package com.tyz.rpc.consumer.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tyz
 * @date 2022/11/10 21:10
 * 引用代理类
 */

@Target(ElementType.FIELD) //作用于字段
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
}
