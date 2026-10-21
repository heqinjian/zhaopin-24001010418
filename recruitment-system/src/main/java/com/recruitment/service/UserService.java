package com.recruitment.service;

import com.recruitment.common.Result;
import com.recruitment.model.User;

public interface UserService {

    // 注册：参数是前端传来的「原始请求 User」，返回统一响应包装的 User（不含密码）
    Result<User> register(User request);

    // 登录：参数是前端传来的 User，返回登录用户信息（不含密码）
    Result<User> login(User request);

    // 按 id 查用户信息
    Result<User> getUserInfo(Long userId);
}
