package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/apply")
    public Result<String> apply(@RequestBody Map<String, Long> request) {
        Long userId = request == null ? null : request.get("userId");
        Long jobId = request == null ? null : request.get("jobId");
        return applicationService.apply(userId, jobId);
    }

    @PutMapping("/cancel")
    public Result<String> cancel(@RequestParam("userId") Long userId,
                                 @RequestParam("jobId") Long jobId) {
        return applicationService.cancel(userId, jobId);
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam("userId") Long userId) {
        return applicationService.listByUser(userId);
    }
}
