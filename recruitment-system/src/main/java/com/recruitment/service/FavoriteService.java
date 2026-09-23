package com.recruitment.service;

import com.recruitment.common.Result;
import com.recruitment.model.Favorite;

import java.util.List;

public interface FavoriteService {

    Result<List<Favorite>> list(Long userId);

    Result<Favorite> add(Favorite favorite);

    Result<Void> cancel(Long userId, Long jobId);
}
