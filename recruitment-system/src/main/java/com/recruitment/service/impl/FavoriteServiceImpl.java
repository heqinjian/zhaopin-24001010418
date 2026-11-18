package com.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recruitment.common.Result;
import com.recruitment.mapper.FavoriteMapper;
import com.recruitment.mapper.JobMapper;
import com.recruitment.mapper.UserMapper;
import com.recruitment.model.Favorite;
import com.recruitment.model.Job;
import com.recruitment.service.FavoriteService;
import com.recruitment.util.RedisUtil;
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

    @Autowired
    private RedisUtil redisUtil;

    // 缓存保留 3 分钟，单位为秒
    private static final long FAVORITE_CACHE_SECONDS = 3 * 60;

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
        // 第 5 步：收藏成功后删除该用户的收藏列表缓存（数据库没成功时不删）
        redisUtil.delete("favorite:list:" + userId);
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
        // 第 4 步：取消成功后删除该用户的收藏列表缓存
        redisUtil.delete("favorite:list:" + userId);
        return Result.success("取消收藏成功", null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<List<Job>> listByUser(Long userId) {
        // 第 1 步：校验 userId
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        String cacheKey = "favorite:list:" + userId;
        // 第 2 步：先查 Redis，命中就直接返回
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            System.out.println("命中Redis缓存：" + cacheKey);
            return Result.success((List<Job>) cached);
        }
        System.out.println("Redis缓存未命中，查询MySQL：" + cacheKey);
        // 第 3 步：未命中，用户必须存在
        if (userMapper.selectById(userId) == null) {
            return Result.error("用户不存在");
        }
        // 第 4 步：查该用户的收藏记录，逐条按 jobId 查职位，组装成 Job 列表
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
        // 第 5 步：写入缓存，3 分钟后过期
        redisUtil.set(cacheKey, jobs, FAVORITE_CACHE_SECONDS);
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
