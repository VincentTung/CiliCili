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

    @ApiModelProperty("密码（已加密）")
    @NotNull
    private String pwd;
    
    @ApiModelProperty("加密类型：MD5/SHA256，默认为MD5")
    private String encryptType = "MD5";
}
