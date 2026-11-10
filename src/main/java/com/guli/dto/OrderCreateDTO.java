package com.guli.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建订单入参
 */
@Data
@Schema(description = "创建订单入参")
public class OrderCreateDTO implements Serializable {

    @NotNull(message = "请选择课程")
    @Schema(description = "课程 ID", example = "1")
    private Long courseId;
}
