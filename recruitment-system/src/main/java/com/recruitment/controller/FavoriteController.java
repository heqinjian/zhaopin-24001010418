package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.model.Job;
import com.recruitment.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Long> request) {
        Long userId = request == null ? null : request.get("userId");
        Long jobId = request == null ? null : request.get("jobId");
        return favoriteService.add(userId, jobId);
    }

    @DeleteMapping("/remove")
    public Result<String> remove(@RequestParam("userId") Long userId,
                                 @RequestParam("jobId") Long jobId) {
        return favoriteService.remove(userId, jobId);
    }

    @GetMapping("/list")
    public Result<List<Job>> list(@RequestParam("userId") Long userId) {
        return favoriteService.listByUser(userId);
    }
}
