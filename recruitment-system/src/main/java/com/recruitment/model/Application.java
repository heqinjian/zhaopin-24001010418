package com.recruitment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Application {

    public static final int STATUS_APPLIED = 0;
    public static final int STATUS_CANCELED = 1;

    private Long id;
    private Long userId;
    private Long jobId;
    private int status;
    private LocalDateTime applyTime;
    private LocalDateTime cancelTime;
}
