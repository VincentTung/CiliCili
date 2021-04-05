package com.vincent.funvideo.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class LoginForm extends BaseForm{
    @ApiModelProperty("用户名")
    @NotNull
    private String username;

    @ApiModelProperty("密码")
    @NotNull
    private String pwd;
}
