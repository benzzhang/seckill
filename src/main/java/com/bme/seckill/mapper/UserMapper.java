package com.bme.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bme.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author benzzhang
 * @since 2022-01-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
