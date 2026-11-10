package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 讲师新增 / 修改入参（新增时 id 为空，修改时 id 必填）
 */
@Data
@Schema(description = "讲师保存入参")
public class TeacherSaveDTO implements Serializable {

    @Schema(description = "讲师ID，修改时必填", example = "1")
    private Long id;

    @NotBlank(message = "讲师姓名不能为空")
    @Size(max = 20, message = "讲师姓名不能超过 20 个字符")
    @Schema(description = "姓名", example = "张明远")
    private String name;

    @Size(max = 500, message = "简介不能超过 500 个字符")
    @Schema(description = "简介")
    private String intro;

    @Size(max = 500, message = "资历不能超过 500 个字符")
    @Schema(description = "资历")
    private String career;

    @Min(value = 1, message = "级别只能是 1 或 2")
    @Max(value = 2, message = "级别只能是 1 或 2")
    @Schema(description = "1=讲师 2=高级讲师", example = "2")
    private Integer level = 1;

    @Schema(description = "头像 URL（先调上传接口拿到）")
    private String avatar;

    @Schema(description = "排序，越小越前", example = "1")
    private Integer sort = 0;
}
