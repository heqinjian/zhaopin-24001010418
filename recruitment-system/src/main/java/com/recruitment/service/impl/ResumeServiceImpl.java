package com.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recruitment.common.Result;
import com.recruitment.mapper.ResumeMapper;
import com.recruitment.mapper.UserMapper;
import com.recruitment.model.Resume;
import com.recruitment.model.User;
import com.recruitment.service.ResumeService;
import com.recruitment.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    // 缓存保留 3 分钟，单位为秒
    private static final long RESUME_CACHE_SECONDS = 3 * 60;

    @Override
    public Result<Resume> create(Resume resume) {
        // 第 1 步：校验——用户 id、真实姓名不能为空
        if (resume == null || resume.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (isBlank(resume.getRealName())) {
            log.warn("创建简历失败: 真实姓名为空, userId={}", resume.getUserId());
            return Result.error("真实姓名不能为空");
        }
        // 第 2 步：用户必须存在
        if (userMapper.selectById(resume.getUserId()) == null) {
            log.warn("创建简历失败: 用户不存在, userId={}", resume.getUserId());
            return Result.error("用户不存在");
        }
        // 第 3 步：补全后端字段——id 由数据库自增，创建/更新时间用服务器时间
        resume.setId(null);
        LocalDateTime now = LocalDateTime.now();
        resume.setCreateTime(now);
        resume.setUpdateTime(now);
        // 第 4 步：插入数据库
        resumeMapper.insert(resume);
        // 只记 id 和所属用户，简历正文不进日志
        log.info("简历创建成功: resumeId={}, userId={}", resume.getId(), resume.getUserId());
        // 第 5 步：插入成功后删除该用户的简历列表缓存（新简历还没有详情缓存）
        redisUtil.delete("resume:list:" + resume.getUserId());
        return Result.success("创建成功", resume);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<Resume> getById(Long id) {
        if (id == null) {
            return Result.error("简历ID不能为空");
        }
        String cacheKey = "resume:detail:" + id;
        // 第 1 步：先查 Redis
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            System.out.println("命中Redis缓存：" + cacheKey);
            return Result.success((Resume) cached);
        }
        System.out.println("Redis缓存未命中，查询MySQL：" + cacheKey);
        // 第 2 步：未命中，查 MySQL
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            // 简历不存在时不写入缓存
            return Result.error("简历不存在");
        }
        // 第 3 步：查到后写入缓存，3 分钟后过期
        redisUtil.set(cacheKey, resume, RESUME_CACHE_SECONDS);
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
        // 第 4 步：更新成功后删除详情缓存和该用户的列表缓存
        redisUtil.delete("resume:detail:" + id);
        redisUtil.delete("resume:list:" + existing.getUserId());
        return Result.success("更新成功", existing);
    }

    @Override
    public Result<String> delete(Long id) {
        if (id == null) {
            return Result.error("简历ID不能为空");
        }
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            return Result.error("简历不存在");
        }
        resumeMapper.deleteById(id);
        // 删除成功后删除详情缓存和该用户的列表缓存
        redisUtil.delete("resume:detail:" + id);
        redisUtil.delete("resume:list:" + resume.getUserId());
        return Result.success("删除成功", "删除成功");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<List<Resume>> listByUserId(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        String cacheKey = "resume:list:" + userId;
        // 第 1 步：先查 Redis，命中就直接返回，不再查 user 表
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            System.out.println("命中Redis缓存：" + cacheKey);
            return Result.success((List<Resume>) cached);
        }
        System.out.println("Redis缓存未命中，查询MySQL：" + cacheKey);
        // 第 2 步：未命中，查 MySQL
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getUserId, userId);
        List<Resume> resumes = resumeMapper.selectList(wrapper);
        // 第 3 步：写入缓存，3 分钟后过期。空列表也写入
        redisUtil.set(cacheKey, resumes, RESUME_CACHE_SECONDS);
        return Result.success(resumes);
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
