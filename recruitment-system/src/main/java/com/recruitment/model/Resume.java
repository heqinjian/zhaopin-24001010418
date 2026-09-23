package com.recruitment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Resume {

    private Long id;
    private Long userId;
    private String realName;
    private Integer gender;
    private String avatar;
    private String phone;
    private String email;
    private String city;
    private String education;
    private String experience;
    private String expectedSalary;
    private String skills;
    private String introduction;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
