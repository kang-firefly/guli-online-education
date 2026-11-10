package com.guli.controller;

import com.guli.common.Result;
import com.guli.common.UserContext;
import com.guli.dto.OrderCreateDTO;
import com.guli.service.OrderService;
import com.guli.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程订单（前台）
 * <p>下单 → 模拟支付 → 解锁播放。所有接口都需要学员登录，且只能操作自己的订单。
 */
@Tag(name = "14-课程订单")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单（已有待支付订单则复用）")
    @PostMapping
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateDTO dto) {
        Long memberId = UserContext.require().getId();
        return Result.ok("下单成功", orderService.create(memberId, dto.getCourseId()));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderNo}")
    public Result<OrderVO> detail(@PathVariable String orderNo) {
        Long memberId = UserContext.require().getId();
        return Result.ok(orderService.getByNo(memberId, orderNo));
    }

    @Operation(summary = "模拟支付（演示环境，点击即成功）")
    @PostMapping("/{orderNo}/pay")
    public Result<OrderVO> pay(@PathVariable String orderNo) {
        Long memberId = UserContext.require().getId();
        return Result.ok("支付成功", orderService.pay(memberId, orderNo));
    }

    @Operation(summary = "取消待支付订单")
    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancel(@PathVariable String orderNo) {
        Long memberId = UserContext.require().getId();
        orderService.cancel(memberId, orderNo);
        return Result.ok("订单已取消", null);
    }

    @Operation(summary = "我的课程（已支付订单）")
    @GetMapping("/my")
    public Result<List<OrderVO>> my() {
        Long memberId = UserContext.require().getId();
        return Result.ok(orderService.myPaidCourses(memberId));
    }
}
