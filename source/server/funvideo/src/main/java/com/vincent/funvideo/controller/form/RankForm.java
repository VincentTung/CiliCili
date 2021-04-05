package com.vincent.funvideo.controller.form;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.awt.print.PageFormat;

@Data
@ApiModel
public class RankForm extends PageForm {

    @ApiModelProperty("分类")
    @NotNull
    private String sort;
}