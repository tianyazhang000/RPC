package com.tyz.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tyz
 * @date 2022/11/10 18:08
 * 封装请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

}
