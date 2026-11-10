package com.guli.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guli.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
