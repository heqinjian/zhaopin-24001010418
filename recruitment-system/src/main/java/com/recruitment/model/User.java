package com.recruitment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    private String userName;
    private String passWord;
    private String nickName;
    private String email;
    private String phone;
    private LocalDateTime createTime;
}
