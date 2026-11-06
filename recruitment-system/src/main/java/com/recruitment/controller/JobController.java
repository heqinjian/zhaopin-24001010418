package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.model.Job;
import com.recruitment.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "职位管理", description = "职位查询相关接口")
@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @Operation(summary = "职位列表", description = "查询职位列表，支持按城市和关键词筛选")
    @GetMapping("/list")
    public Result<List<Job>> list(
            @Parameter(description = "城市") @RequestParam(value = "city", required = false) String city,
            @Parameter(description = "关键词") @RequestParam(value = "keyword", required = false) String keyword) {
        return jobService.list(city, keyword);
    }

    @Operation(summary = "职位详情", description = "根据ID查询职位详情")
    @GetMapping("/{id}")
    public Result<Job> detail(@Parameter(description = "职位ID") @PathVariable Long id) {
        return jobService.detail(id);
    }
}
