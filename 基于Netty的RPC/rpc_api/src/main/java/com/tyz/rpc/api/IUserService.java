package com.tyz.rpc.api;

import com.tyz.rpc.pojo.User;

/**
 * @author tyz
 * @date 2022/11/10 18:05
 * 用户服务
 */
public interface IUserService {

    /**
     * @author tyz
     * @date 2022/11/10 18:06
     * 根据用户ID查询用户
     */
    User getById(int id);

}
