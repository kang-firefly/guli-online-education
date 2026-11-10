package com.guli.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单展示对象
 */
@Data
@Schema(description = "订单")
public class OrderVO implements Serializable {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "课程 ID")
    private Long courseId;

    @Schema(description = "课程标题（下单时快照）")
    private String courseTitle;

    @Schema(description = "封面（下单时快照）")
    private String cover;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "0 待支付 / 1 已支付 / 2 已取消")
    private Integer status;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "下单时间")
    private LocalDateTime gmtCreate;
}
