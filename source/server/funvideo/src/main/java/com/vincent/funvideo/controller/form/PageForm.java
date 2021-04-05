package com.vincent.funvideo.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class PageForm extends BaseForm{

    @ApiModelProperty("页码")
    private Integer pageIndex;

    @ApiModelProperty("每页条数")
    private Integer pageSize;
}