package com.guli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程订单
 * <p>courseTitle / cover 为下单时的快照，课程改名或换封面后历史订单仍显示当时的信息。
 */
@Data
@TableName("edu_order")
public class Order implements Serializable {

    /** 状态：待支付 */
    public static final int STATUS_UNPAID = 0;
    /** 状态：已支付 */
    public static final int STATUS_PAID = 1;
    /** 状态：已取消 */
    public static final int STATUS_CANCELLED = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long memberId;

    private Long courseId;

    /** 下单时的课程标题快照 */
    private String courseTitle;

    /** 下单时的封面快照 */
    private String cover;

    private BigDecimal amount;

    /** 0 待支付 / 1 已支付 / 2 已取消 */
    private Integer status;

    /** MOCK = 模拟支付 */
    private String payType;

    private LocalDateTime payTime;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
