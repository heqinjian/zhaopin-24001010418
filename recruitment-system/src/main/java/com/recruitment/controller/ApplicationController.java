package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.model.Application;
import com.recruitment.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/list")
    public Result<List<Application>> list(@RequestParam("userId") Long userId) {
        return applicationService.list(userId);
    }

    @PostMapping
    public Result<Application> apply(@RequestBody Application application) {
        return applicationService.apply(application);
    }

    @DeleteMapping("/{userId}/{jobId}")
    public Result<Void> cancel(@PathVariable Long userId, @PathVariable Long jobId) {
        return applicationService.cancel(userId, jobId);
    }
}
