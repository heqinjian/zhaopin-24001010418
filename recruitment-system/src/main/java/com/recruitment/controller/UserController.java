package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.context.UserContext;
import com.recruitment.model.User;
import com.recruitment.service.UserService;
import com.recruitment.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "用户注册、登录相关接口")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "新用户注册账号")
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody User request) {
        return userService.register(request);
    }

    @Operation(summary = "用户登录", description = "用户登录验证，成功返回 token 与用户信息")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody User request) {
        return userService.login(request);
    }

    @Operation(summary = "按ID查询用户", description = "根据用户ID查询用户信息")
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        return userService.getUserInfo(id);
    }

    @Operation(summary = "获取当前登录用户", description = "根据 token 解析当前登录用户信息")
    @GetMapping("/info")
    public Result<User> getCurrentUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        return userService.getUserInfo(userId);
    }
}
