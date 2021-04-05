package com.vincent.funvideo.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel
public class DetailFrom extends BaseForm {

    @ApiModelProperty("视频id")
    @NotNull
    private Integer id;


}