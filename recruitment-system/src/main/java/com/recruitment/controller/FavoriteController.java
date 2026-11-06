package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.model.Job;
import com.recruitment.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "收藏管理", description = "职位收藏相关接口")
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Operation(summary = "添加收藏", description = "用户收藏职位，Body 传 userId、jobId")
    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Long> request) {
        Long userId = request == null ? null : request.get("userId");
        Long jobId = request == null ? null : request.get("jobId");
        return favoriteService.add(userId, jobId);
    }

    @Operation(summary = "取消收藏", description = "根据 userId、jobId 物理删除收藏记录")
    @DeleteMapping("/remove")
    public Result<String> remove(@Parameter(description = "用户ID") @RequestParam("userId") Long userId,
                                 @Parameter(description = "职位ID") @RequestParam("jobId") Long jobId) {
        return favoriteService.remove(userId, jobId);
    }

    @Operation(summary = "收藏列表", description = "查询用户收藏的职位列表")
    @GetMapping("/list")
    public Result<List<Job>> list(@Parameter(description = "用户ID") @RequestParam("userId") Long userId) {
        return favoriteService.listByUser(userId);
    }
}
