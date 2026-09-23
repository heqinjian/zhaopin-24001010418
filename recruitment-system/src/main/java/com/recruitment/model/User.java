package com.recruitment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private LocalDateTime createTime;
}
