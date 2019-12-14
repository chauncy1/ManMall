package com.man.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {

    @NotBlank(message = "账号不能为空")
    private String userAccount;

    @NotBlank(message = "用户名称不能为空")
    private String userName;

    @NotBlank(message = "输密码啊，老哥")
    private String userPassword;

    @Size(min = 0, max = 50)
    private String userDetail;

    @Min(value = 0)
    @PositiveOrZero
    private Integer userScore;

}

