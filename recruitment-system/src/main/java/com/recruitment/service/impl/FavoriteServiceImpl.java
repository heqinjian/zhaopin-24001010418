package com.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recruitment.common.Result;
import com.recruitment.mapper.FavoriteMapper;
import com.recruitment.mapper.JobMapper;
import com.recruitment.mapper.UserMapper;
import com.recruitment.model.Favorite;
import com.recruitment.model.Job;
import com.recruitment.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JobMapper jobMapper;

    @Override
    public Result<String> add(Long userId, Long jobId) {
        // 第 1 步：两个 id 都不能为空
        if (userId == null || jobId == null) {
            return Result.error("用户ID和职位ID不能为空");
        }
        // 第 2 步：用户、职位都要存在
        if (userMapper.selectById(userId) == null || jobMapper.selectById(jobId) == null) {
            return Result.error("用户或职位不存在");
        }
        // 第 3 步：同一用户 + 同一职位只能收藏一次
        if (findFavorite(userId, jobId) != null) {
            return Result.error("已收藏");
        }
        // 第 4 步：插入收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setJobId(jobId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
        return Result.success("收藏成功", null);
    }

    @Override
    public Result<String> remove(Long userId, Long jobId) {
        // 第 1 步：校验 id
        if (userId == null || jobId == null) {
            return Result.error("用户ID和职位ID不能为空");
        }
        // 第 2 步：记录必须存在
        Favorite favorite = findFavorite(userId, jobId);
        if (favorite == null) {
            return Result.error("收藏记录不存在");
        }
        // 第 3 步：物理删除
        favoriteMapper.deleteById(favorite.getId());
        return Result.success("取消收藏成功", null);
    }

    @Override
    public Result<List<Job>> listByUser(Long userId) {
        // 第 1 步：校验 userId
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        // 第 2 步：用户必须存在
        if (userMapper.selectById(userId) == null) {
            return Result.error("用户不存在");
        }
        // 第 3 步：查该用户的收藏记录，逐条按 jobId 查职位，组装成 Job 列表
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        List<Job> jobs = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Job job = jobMapper.selectById(favorite.getJobId());
            if (job != null) {
                jobs.add(job);
            }
        }
        return Result.success(jobs);
    }

    /** 查指定 userId + jobId 的收藏记录，没有则 null */
    private Favorite findFavorite(Long userId, Long jobId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getJobId, jobId);
        return favoriteMapper.selectOne(wrapper);
    }
}
