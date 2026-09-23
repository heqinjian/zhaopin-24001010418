package com.recruitment.service;

import com.recruitment.common.Result;
import com.recruitment.model.Application;

import java.util.List;

public interface ApplicationService {

    Result<List<Application>> list(Long userId);

    Result<Application> apply(Application application);

    Result<Void> cancel(Long userId, Long jobId);
}
