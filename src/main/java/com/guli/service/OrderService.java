package com.guli.service;

import com.guli.vo.OrderVO;

import java.util.List;

/**
 * 课程订单（前台）
 */
public interface OrderService {

    /** 创建订单；若已有待支付订单则直接复用 */
    OrderVO create(Long memberId, Long courseId);

    /** 查订单（仅本人） */
    OrderVO getByNo(Long memberId, String orderNo);

    /** 模拟支付 */
    OrderVO pay(Long memberId, String orderNo);

    /** 取消待支付订单 */
    void cancel(Long memberId, String orderNo);

    /** 我的课程：已支付订单 */
    List<OrderVO> myPaidCourses(Long memberId);

    /** 是否已购买某课程 */
    boolean hasPaid(Long memberId, Long courseId);

    /** 该会员对该课程是否有待支付订单，返回订单号或 null */
    String findUnpaidOrderNo(Long memberId, Long courseId);
}
