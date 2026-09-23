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

    @GetMapping("/list")
    public Result<List<Resume>> list(@RequestParam("userId") Long userId) {
        return resumeService.list(userId);
    }

    @GetMapping("/{id}")
    public Result<Resume> detail(@PathVariable Long id) {
        return resumeService.detail(id);
    }

    @PostMapping
    public Result<Resume> create(@RequestBody Resume resume) {
        return resumeService.create(resume);
    }

    @PutMapping("/{id}")
    public Result<Resume> update(@PathVariable Long id, @RequestBody Resume resume) {
        return resumeService.update(id, resume);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return resumeService.delete(id);
    }
}
