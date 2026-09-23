package com.recruitment.service.impl;

import com.recruitment.common.Result;
import com.recruitment.data.MockDataStore;
import com.recruitment.model.User;
import com.recruitment.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public Result<User> register(User user) {
        if (user == null || isBlank(user.getUsername()) || isBlank(user.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }
        if (MockDataStore.getUserByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        user.setId(MockDataStore.nextUserId());
        user.setCreateTime(LocalDateTime.now());
        MockDataStore.saveUser(user);
        return Result.success("注册成功", user);
    }

    @Override
    public Result<User> login(User user) {
        if (user == null || isBlank(user.getUsername()) || isBlank(user.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }
        User exist = MockDataStore.getUserByUsername(user.getUsername());
        if (exist == null || !user.getPassword().equals(exist.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        return Result.success("登录成功", exist);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
