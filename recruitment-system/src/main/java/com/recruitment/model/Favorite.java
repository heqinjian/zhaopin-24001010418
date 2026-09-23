package com.recruitment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Favorite {

    private Long id;
    private Long userId;
    private Long jobId;
    private LocalDateTime createTime;
}
