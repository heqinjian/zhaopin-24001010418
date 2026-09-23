package com.recruitment.service.impl;

import com.recruitment.common.Result;
import com.recruitment.data.MockDataStore;
import com.recruitment.model.Application;
import com.recruitment.model.Job;
import com.recruitment.service.ApplicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Override
    public Result<List<Application>> list(Long userId) {
        if (userId == null) {
            return Result.error("userId 不能为空");
        }
        List<Application> result = MockDataStore.getApplications().stream()
                .filter(item -> userId.equals(item.getUserId()))
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @Override
    public Result<Application> apply(Application application) {
        if (application == null || application.getUserId() == null || application.getJobId() == null) {
            return Result.error("userId 和 jobId 不能为空");
        }
        if (MockDataStore.getUserById(application.getUserId()) == null) {
            return Result.error("用户不存在");
        }
        Job job = MockDataStore.getJobById(application.getJobId());
        if (job == null) {
            return Result.error("职位不存在");
        }
        boolean applied = MockDataStore.getApplications().stream()
                .anyMatch(item -> application.getUserId().equals(item.getUserId())
                        && application.getJobId().equals(item.getJobId())
                        && item.getStatus() == Application.STATUS_APPLIED);
        if (applied) {
            return Result.error("已经投递过该职位");
        }
        application.setId(MockDataStore.nextApplicationId());
        application.setStatus(Application.STATUS_APPLIED);
        application.setApplyTime(LocalDateTime.now());
        application.setCancelTime(null);
        MockDataStore.getApplications().add(application);
        job.setApplyCount(job.getApplyCount() + 1);
        return Result.success("投递成功", application);
    }

    @Override
    public Result<Void> cancel(Long userId, Long jobId) {
        Application application = MockDataStore.getApplications().stream()
                .filter(item -> userId != null && userId.equals(item.getUserId())
                        && jobId != null && jobId.equals(item.getJobId())
                        && item.getStatus() == Application.STATUS_APPLIED)
                .findFirst()
                .orElse(null);
        if (application == null) {
            return Result.error("投递记录不存在或已取消");
        }
        application.setStatus(Application.STATUS_CANCELED);
        application.setCancelTime(LocalDateTime.now());
        Job job = MockDataStore.getJobById(jobId);
        if (job != null && job.getApplyCount() > 0) {
            job.setApplyCount(job.getApplyCount() - 1);
        }
        return Result.success("取消投递成功", null);
    }
}
