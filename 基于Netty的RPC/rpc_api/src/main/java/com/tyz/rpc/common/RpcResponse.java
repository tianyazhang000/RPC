package com.tyz.rpc.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tyz
 * @date 2022/11/10 18:12
 * 封装响应对象
 */
@Data
@NoArgsConstructor
public class RpcResponse {

    private String requestId;

    private String error;

    private Object result;

}
