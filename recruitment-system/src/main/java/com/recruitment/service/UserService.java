package com.recruitment.service;

import com.recruitment.common.Result;
import com.recruitment.model.User;

public interface UserService {

    Result<User> register(User user);

    Result<User> login(User user);
}
