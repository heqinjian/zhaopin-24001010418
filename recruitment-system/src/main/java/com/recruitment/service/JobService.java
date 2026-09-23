package com.recruitment.service;

import com.recruitment.common.Result;
import com.recruitment.model.Job;

import java.util.List;

public interface JobService {

    Result<List<Job>> list(String city, String keyword);

    Result<Job> detail(Long id);
}
