package com.recruitment.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("application")
public class Application {

    public static final int STATUS_APPLIED = 0;
    public static final int STATUS_CANCELED = 1;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long jobId;
    private int status;
    private LocalDateTime applyTime;
    private LocalDateTime cancelTime;
}
