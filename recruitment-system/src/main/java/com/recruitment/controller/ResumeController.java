package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.model.Resume;
import com.recruitment.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "简历管理", description = "简历增删改查相关接口")
@RestController
@RequestMapping("/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Operation(summary = "创建简历", description = "新建简历，userId、realName 必填")
    @PostMapping
    public Result<Resume> create(@RequestBody Resume resume) {
        return resumeService.create(resume);
    }

    @Operation(summary = "简历详情", description = "根据ID查询简历")
    @GetMapping("/{id}")
    public Result<Resume> getById(@Parameter(description = "简历ID") @PathVariable Long id) {
        return resumeService.getById(id);
    }

    @Operation(summary = "更新简历", description = "部分更新，仅覆盖请求体中非 null 字段")
    @PutMapping("/{id}")
    public Result<Resume> update(@Parameter(description = "简历ID") @PathVariable Long id,
                                 @RequestBody Resume resume) {
        return resumeService.update(id, resume);
    }

    @Operation(summary = "删除简历", description = "根据ID删除简历")
    @DeleteMapping("/{id}")
    public Result<String> delete(@Parameter(description = "简历ID") @PathVariable Long id) {
        return resumeService.delete(id);
    }

    @Operation(summary = "用户简历列表", description = "查询某用户的全部简历")
    @GetMapping("/list")
    public Result<List<Resume>> listByUserId(@Parameter(description = "用户ID") @RequestParam("userId") Long userId) {
        return resumeService.listByUserId(userId);
    }
}
