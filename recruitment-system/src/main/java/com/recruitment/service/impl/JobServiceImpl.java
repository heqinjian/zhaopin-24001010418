package com.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recruitment.common.Result;
import com.recruitment.mapper.JobMapper;
import com.recruitment.model.Job;
import com.recruitment.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public Result<List<Job>> list(String city, String keyword) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        // 城市精确匹配
        if (city != null && !city.trim().isEmpty()) {
            wrapper.eq(Job::getCity, city);
        }
        // 关键字在公司名、职位名中模糊匹配
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Job::getCompanyName, keyword)
                    .or()
                    .like(Job::getJobTitle, keyword));
        }
        return Result.success(jobMapper.selectList(wrapper));
    }

    @Override
    public Result<Job> detail(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            return Result.error("职位不存在");
        }
        // 浏览量 +1 并更新数据库
        job.setViewCount(job.getViewCount() + 1);
        jobMapper.updateById(job);
        return Result.success(job);
    }
}
