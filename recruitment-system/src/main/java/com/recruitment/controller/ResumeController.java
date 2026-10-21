package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.model.Resume;
import com.recruitment.service.ResumeService;
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

@RestController
@RequestMapping("/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    // POST，无子路径 → 创建；参数从请求体 JSON 绑定
    @PostMapping
    public Result<Resume> create(@RequestBody Resume resume) {
        return resumeService.create(resume);
    }

    // GET，路径带 {id}；id 从 URL 路径取
    @GetMapping("/{id}")
    public Result<Resume> getById(@PathVariable Long id) {
        return resumeService.getById(id);
    }

    // PUT，路径带 {id}；路径 id + 请求体 JSON（部分字段）
    @PutMapping("/{id}")
    public Result<Resume> update(@PathVariable Long id, @RequestBody Resume resume) {
        return resumeService.update(id, resume);
    }

    // DELETE，路径带 {id}；id 从 URL 路径取
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return resumeService.delete(id);
    }

    // GET，子路径用 /list；userId 从 ?userId= 取
    @GetMapping("/list")
    public Result<List<Resume>> listByUserId(@RequestParam("userId") Long userId) {
        return resumeService.listByUserId(userId);
    }
}
