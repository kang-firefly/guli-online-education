package com.guli.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guli.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * Member Mapper
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
