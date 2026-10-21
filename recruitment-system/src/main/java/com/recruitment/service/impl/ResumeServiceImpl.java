package com.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recruitment.common.Result;
import com.recruitment.mapper.ResumeMapper;
import com.recruitment.mapper.UserMapper;
import com.recruitment.model.Resume;
import com.recruitment.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<Resume> create(Resume resume) {
        // 第 1 步：校验——用户 id、真实姓名不能为空
        if (resume == null || resume.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (isBlank(resume.getRealName())) {
            return Result.error("真实姓名不能为空");
        }
        // 第 2 步：用户必须存在
        if (userMapper.selectById(resume.getUserId()) == null) {
            return Result.error("用户不存在");
        }
        // 第 3 步：补全后端字段——id 由数据库自增，创建/更新时间用服务器时间
        resume.setId(null);
        LocalDateTime now = LocalDateTime.now();
        resume.setCreateTime(now);
        resume.setUpdateTime(now);
        // 第 4 步：插入数据库
        resumeMapper.insert(resume);
        return Result.success("创建成功", resume);
    }

    @Override
    public Result<Resume> getById(Long id) {
        if (id == null) {
            return Result.error("简历ID不能为空");
        }
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            return Result.error("简历不存在");
        }
        return Result.success(resume);
    }

    @Override
    public Result<Resume> update(Long id, Resume resume) {
        // 第 1 步：校验 id，并取出原记录
        if (id == null) {
            return Result.error("简历ID不能为空");
        }
        Resume existing = resumeMapper.selectById(id);
        if (existing == null) {
            return Result.error("简历不存在");
        }
        // 第 2 步：部分更新——请求体里非 null 的字段才覆盖；不改 id、userId、createTime
        if (resume.getRealName() != null) {
            existing.setRealName(resume.getRealName());
        }
        if (resume.getGender() != null) {
            existing.setGender(resume.getGender());
        }
        if (resume.getAvatar() != null) {
            existing.setAvatar(resume.getAvatar());
        }
        if (resume.getPhone() != null) {
            existing.setPhone(resume.getPhone());
        }
        if (resume.getEmail() != null) {
            existing.setEmail(resume.getEmail());
        }
        if (resume.getCity() != null) {
            existing.setCity(resume.getCity());
        }
        if (resume.getEducation() != null) {
            existing.setEducation(resume.getEducation());
        }
        if (resume.getExperience() != null) {
            existing.setExperience(resume.getExperience());
        }
        if (resume.getExpectedSalary() != null) {
            existing.setExpectedSalary(resume.getExpectedSalary());
        }
        if (resume.getSkills() != null) {
            existing.setSkills(resume.getSkills());
        }
        if (resume.getIntroduction() != null) {
            existing.setIntroduction(resume.getIntroduction());
        }
        // 第 3 步：刷新更新时间，写回数据库
        existing.setUpdateTime(LocalDateTime.now());
        resumeMapper.updateById(existing);
        return Result.success("更新成功", existing);
    }

    @Override
    public Result<String> delete(Long id) {
        if (id == null) {
            return Result.error("简历ID不能为空");
        }
        if (resumeMapper.selectById(id) == null) {
            return Result.error("简历不存在");
        }
        resumeMapper.deleteById(id);
        return Result.success("删除成功", "删除成功");
    }

    @Override
    public Result<List<Resume>> listByUserId(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        if (userMapper.selectById(userId) == null) {
            return Result.error("用户不存在");
        }
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getUserId, userId);
        return Result.success(resumeMapper.selectList(wrapper));
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
