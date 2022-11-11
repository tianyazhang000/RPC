package com.tyz.rpc.provider.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tyz
 * @date 2022/11/10 18:33
 * 用于暴露服务接口
 */
@Target(ElementType.TYPE)//用于类
@Retention(RetentionPolicy.RUNTIME)//在运行时获取
public @interface RpcService {
}
