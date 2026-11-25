package com.recruitment.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("resume")
@Schema(description = "简历信息")
public class Resume {

    @Schema(description = "简历ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "所在城市")
    private String city;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "工作经历")
    private String experience;

    @Schema(description = "期望薪资")
    private String expectedSalary;

    @Schema(description = "技能标签")
    private String skills;

    @Schema(description = "自我介绍")
    private String introduction;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
