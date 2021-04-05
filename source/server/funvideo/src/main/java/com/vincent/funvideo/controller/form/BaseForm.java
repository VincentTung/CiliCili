package com.vincent.funvideo.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class BaseForm {

    @ApiModelProperty("apikey")
    @NotNull
    private String apikey;
}
