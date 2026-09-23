package com.recruitment.service.impl;

import com.recruitment.common.Result;
import com.recruitment.data.MockDataStore;
import com.recruitment.model.Favorite;
import com.recruitment.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Override
    public Result<List<Favorite>> list(Long userId) {
        if (userId == null) {
            return Result.error("userId 不能为空");
        }
        List<Favorite> result = MockDataStore.getFavorites().stream()
                .filter(item -> userId.equals(item.getUserId()))
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @Override
    public Result<Favorite> add(Favorite favorite) {
        if (favorite == null || favorite.getUserId() == null || favorite.getJobId() == null) {
            return Result.error("userId 和 jobId 不能为空");
        }
        if (MockDataStore.getUserById(favorite.getUserId()) == null) {
            return Result.error("用户不存在");
        }
        if (MockDataStore.getJobById(favorite.getJobId()) == null) {
            return Result.error("职位不存在");
        }
        boolean exists = MockDataStore.getFavorites().stream()
                .anyMatch(item -> favorite.getUserId().equals(item.getUserId())
                        && favorite.getJobId().equals(item.getJobId()));
        if (exists) {
            return Result.error("已经收藏过该职位");
        }
        favorite.setId(MockDataStore.nextFavoriteId());
        favorite.setCreateTime(LocalDateTime.now());
        MockDataStore.getFavorites().add(favorite);
        return Result.success("收藏成功", favorite);
    }

    @Override
    public Result<Void> cancel(Long userId, Long jobId) {
        boolean removed = MockDataStore.getFavorites().removeIf(item ->
                userId != null && userId.equals(item.getUserId())
                        && jobId != null && jobId.equals(item.getJobId()));
        if (!removed) {
            return Result.error("收藏不存在");
        }
        return Result.success("取消收藏成功", null);
    }
}
