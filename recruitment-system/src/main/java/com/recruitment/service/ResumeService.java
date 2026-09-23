package com.recruitment.service;

import com.recruitment.common.Result;
import com.recruitment.model.Resume;

import java.util.List;

public interface ResumeService {

    Result<List<Resume>> list(Long userId);

    Result<Resume> detail(Long id);

    Result<Resume> create(Resume resume);

    Result<Resume> update(Long id, Resume resume);

    Result<Void> delete(Long id);
}
