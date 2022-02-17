package com.bme.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bme.seckill.pojo.SeckillOrder;
import com.bme.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author benzzhang
 * @since 2022-02-11
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
