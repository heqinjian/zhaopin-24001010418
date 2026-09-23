package com.recruitment.service.impl;

import com.recruitment.common.Result;
import com.recruitment.data.MockDataStore;
import com.recruitment.model.Resume;
import com.recruitment.service.ResumeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Override
    public Result<List<Resume>> list(Long userId) {
        if (userId == null) {
            return Result.error("userId 不能为空");
        }
        return Result.success(MockDataStore.getResumesByUserId(userId));
    }

    @Override
    public Result<Resume> detail(Long id) {
        Resume resume = MockDataStore.getResumeById(id);
        if (resume == null) {
            return Result.error("简历不存在");
        }
        return Result.success(resume);
    }

    @Override
    public Result<Resume> create(Resume resume) {
        if (resume == null || resume.getUserId() == null) {
            return Result.error("userId 不能为空");
        }
        if (MockDataStore.getUserById(resume.getUserId()) == null) {
            return Result.error("用户不存在");
        }
        resume.setId(MockDataStore.nextResumeId());
        LocalDateTime now = LocalDateTime.now();
        resume.setCreateTime(now);
        resume.setUpdateTime(now);
        MockDataStore.saveResume(resume);
        return Result.success("创建成功", resume);
    }

    @Override
    public Result<Resume> update(Long id, Resume resume) {
        Resume exist = MockDataStore.getResumeById(id);
        if (exist == null) {
            return Result.error("简历不存在");
        }
        resume.setId(id);
        resume.setUserId(exist.getUserId());
        resume.setCreateTime(exist.getCreateTime());
        resume.setUpdateTime(LocalDateTime.now());
        MockDataStore.saveResume(resume);
        return Result.success("更新成功", resume);
    }

    @Override
    public Result<Void> delete(Long id) {
        Resume exist = MockDataStore.getResumeById(id);
        if (exist == null) {
            return Result.error("简历不存在");
        }
        MockDataStore.deleteResume(id);
        return Result.success("删除成功", null);
    }
}
