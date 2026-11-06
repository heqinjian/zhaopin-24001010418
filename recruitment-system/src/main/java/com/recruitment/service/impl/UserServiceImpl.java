package com.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recruitment.common.Result;
import com.recruitment.mapper.UserMapper;
import com.recruitment.model.User;
import com.recruitment.service.UserService;
import com.recruitment.util.JwtUtil;
import com.recruitment.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result<User> register(User request) {
        // 第 1 步：校验参数——用户名、密码不能为空
        if (request == null || isBlank(request.getUserName()) || isBlank(request.getPassWord())) {
            return Result.error("用户名和密码不能为空");
        }
        // 第 2 步：查重——按用户名查库
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, request.getUserName());
        if (userMapper.selectOne(wrapper) != null) {
            return Result.error("用户名已存在");
        }
        // 第 3 步：新建用户对象（id 由数据库自增，不手动设置）
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassWord(request.getPassWord());
        user.setNickName(request.getNickName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setCreateTime(LocalDateTime.now());
        // 第 4 步：插入数据库；insert 后自增 id 会回填到 user
        userMapper.insert(user);
        // 第 5 步：返回前脱敏，不回传密码
        return Result.success("注册成功", maskPassword(user));
    }

    @Override
    public Result<LoginVO> login(User request) {
        // 第 1 步：校验参数
        if (request == null || isBlank(request.getUserName()) || isBlank(request.getPassWord())) {
            return Result.error("用户名和密码不能为空");
        }
        // 第 2 步：按用户名查库
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, request.getUserName());
        User user = userMapper.selectOne(wrapper);
        // 第 3 步：人不存在或密码不对，统一提示「用户名或密码错误」
        if (user == null || !user.getPassWord().equals(request.getPassWord())) {
            return Result.error("用户名或密码错误");
        }
        // 第 4 步：登录成功，生成 token，返回 token 与脱敏用户
        User safeUser = maskPassword(user);
        String token = jwtUtil.generateToken(user.getId());
        LoginVO loginVO = new LoginVO(token, safeUser);
        return Result.success("登录成功", loginVO);
    }

    @Override
    public Result<User> getUserInfo(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(maskPassword(user));
    }

    /**
     * 复制一份不含密码的 User 对象返回，避免修改原对象。
     */
    private User maskPassword(User user) {
        User safe = new User();
        safe.setId(user.getId());
        safe.setUserName(user.getUserName());
        safe.setNickName(user.getNickName());
        safe.setEmail(user.getEmail());
        safe.setPhone(user.getPhone());
        safe.setCreateTime(user.getCreateTime());
        // 故意不 set passWord，保持 null
        return safe;
    }

    /** 判断字符串是否为空：null 或全为空白 */
    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
