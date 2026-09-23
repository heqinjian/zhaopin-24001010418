package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.model.Favorite;
import com.recruitment.service.FavoriteService;
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
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/list")
    public Result<List<Favorite>> list(@RequestParam("userId") Long userId) {
        return favoriteService.list(userId);
    }

    @PostMapping
    public Result<Favorite> add(@RequestBody Favorite favorite) {
        return favoriteService.add(favorite);
    }

    @DeleteMapping("/{userId}/{jobId}")
    public Result<Void> cancel(@PathVariable Long userId, @PathVariable Long jobId) {
        return favoriteService.cancel(userId, jobId);
    }
}
