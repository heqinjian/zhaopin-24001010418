package com.recruitment.vo;

import com.recruitment.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录返回结果")
public class LoginVO {

    @Schema(description = "JWT 令牌")
    private String token;

    @Schema(description = "用户信息（不含密码）")
    private User user;
}
