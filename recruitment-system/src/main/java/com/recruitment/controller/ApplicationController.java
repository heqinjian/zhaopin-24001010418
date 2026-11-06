package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "投递管理", description = "职位投递相关接口")
@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Operation(summary = "投递职位", description = "用户投递职位，Body 传 userId、jobId；取消后可重新投递")
    @PostMapping("/apply")
    public Result<String> apply(@RequestBody Map<String, Long> request) {
        Long userId = request == null ? null : request.get("userId");
        Long jobId = request == null ? null : request.get("jobId");
        return applicationService.apply(userId, jobId);
    }

    @Operation(summary = "取消投递", description = "只修改状态为已取消，不删除记录")
    @PutMapping("/cancel")
    public Result<String> cancel(@Parameter(description = "用户ID") @RequestParam("userId") Long userId,
                                 @Parameter(description = "职位ID") @RequestParam("jobId") Long jobId) {
        return applicationService.cancel(userId, jobId);
    }

    @Operation(summary = "投递列表", description = "查询用户的投递记录，含职位信息、状态与时间")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@Parameter(description = "用户ID") @RequestParam("userId") Long userId) {
        return applicationService.listByUser(userId);
    }
}
