package com.vincent.funvideo.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class SearchForm extends BaseForm {
    @ApiModelProperty("关键字")
    @NotNull
    private String keyword;

}