package com.vincent.funvideo.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class RecordForm extends BaseForm{
    @ApiModelProperty("video id")
    @NotNull
    @Min(1)
    private Integer vid;
}
