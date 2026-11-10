package com.guli.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.guli.common.BusinessException;
import com.guli.dto.OrderCreateDTO;
import com.guli.entity.Course;
import com.guli.entity.Order;
import com.guli.mapper.CourseMapper;
import com.guli.mapper.OrderMapper;
import com.guli.service.OrderService;
import com.guli.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 课程订单实现（模拟支付）
 * <p>没有接真实支付网关：pay() 直接把订单置为已支付，仅用于演示完整的状态流转。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderMapper orderMapper;
    private final CourseMapper courseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(Long memberId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (course.getStatus() == null || course.getStatus() != 1) {
            throw new BusinessException("课程尚未发布，暂不能购买");
        }
        BigDecimal price = course.getPrice() == null ? BigDecimal.ZERO : course.getPrice();
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("该课程免费，无需购买");
        }
        if (hasPaid(memberId, courseId)) {
            throw new BusinessException("你已购买过该课程");
        }

        // 已有待支付订单则复用，避免重复下单
        Order exist = findUnpaid(memberId, courseId);
        if (exist != null) {
            return toVO(exist);
        }

        Order order = new Order();
        order.setOrderNo(genOrderNo());
        order.setMemberId(memberId);
        order.setCourseId(courseId);
        order.setCourseTitle(course.getTitle());
        order.setCover(course.getCover());
        order.setAmount(price);
        order.setStatus(Order.STATUS_UNPAID);
        orderMapper.insert(order);
        log.info("创建订单 no={} member={} course={} amount={}",
                order.getOrderNo(), memberId, courseId, price);
        return toVO(order);
    }

    @Override
    public OrderVO getByNo(Long memberId, String orderNo) {
        return toVO(requireOwn(memberId, orderNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO pay(Long memberId, String orderNo) {
        Order order = requireOwn(memberId, orderNo);
        if (order.getStatus() == Order.STATUS_PAID) {
            return toVO(order);   // 幂等：重复支付直接返回
        }
        if (order.getStatus() != Order.STATUS_UNPAID) {
            throw new BusinessException("订单已取消，不能支付");
        }

        order.setStatus(Order.STATUS_PAID);
        order.setPayType("MOCK");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 购买数 +1（用 SQL 自增，避免并发覆盖）
        courseMapper.update(null, Wrappers.<Course>lambdaUpdate()
                .setSql("buy_count = buy_count + 1")
                .eq(Course::getId, order.getCourseId()));

        log.info("模拟支付成功 no={} amount={}", orderNo, order.getAmount());
        return toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long memberId, String orderNo) {
        Order order = requireOwn(memberId, orderNo);
        if (order.getStatus() != Order.STATUS_UNPAID) {
            throw new BusinessException("只有待支付订单可以取消");
        }
        order.setStatus(Order.STATUS_CANCELLED);
        orderMapper.updateById(order);
        log.info("取消订单 no={}", orderNo);
    }

    @Override
    public List<OrderVO> myPaidCourses(Long memberId) {
        List<Order> rows = orderMapper.selectList(Wrappers.<Order>lambdaQuery()
                .eq(Order::getMemberId, memberId)
                .eq(Order::getStatus, Order.STATUS_PAID)
                .orderByDesc(Order::getPayTime));
        return rows.stream().map(this::toVO).toList();
    }

    @Override
    public boolean hasPaid(Long memberId, Long courseId) {
        if (memberId == null || courseId == null) {
            return false;
        }
        Long n = orderMapper.selectCount(Wrappers.<Order>lambdaQuery()
                .eq(Order::getMemberId, memberId)
                .eq(Order::getCourseId, courseId)
                .eq(Order::getStatus, Order.STATUS_PAID));
        return n != null && n > 0;
    }

    @Override
    public String findUnpaidOrderNo(Long memberId, Long courseId) {
        Order o = findUnpaid(memberId, courseId);
        return o == null ? null : o.getOrderNo();
    }

    // ---------------- 内部方法 ----------------

    private Order findUnpaid(Long memberId, Long courseId) {
        return orderMapper.selectOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getMemberId, memberId)
                .eq(Order::getCourseId, courseId)
                .eq(Order::getStatus, Order.STATUS_UNPAID)
                .orderByDesc(Order::getId)
                .last("limit 1"));
    }

    private Order requireOwn(Long memberId, String orderNo) {
        Order order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 越权保护：只能操作自己的订单
        if (!order.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作该订单");
        }
        return order;
    }

    /** yyyyMMddHHmmss + 6 位随机数字 */
    private String genOrderNo() {
        return LocalDateTime.now().format(NO_FMT)
                + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
    }

    private OrderVO toVO(Order o) {
        OrderVO vo = new OrderVO();
        vo.setOrderNo(o.getOrderNo());
        vo.setCourseId(o.getCourseId());
        vo.setCourseTitle(o.getCourseTitle());
        vo.setCover(o.getCover());
        vo.setAmount(o.getAmount());
        vo.setStatus(o.getStatus());
        vo.setPayType(o.getPayType());
        vo.setPayTime(o.getPayTime());
        vo.setGmtCreate(o.getGmtCreate());
        return vo;
    }
}
