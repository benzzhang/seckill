package com.bme.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bme.seckill.pojo.Order;
import com.bme.seckill.pojo.User;
import com.bme.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author benzzhang
 * @since 2022-02-11
 */
public interface IOrderService extends IService<Order> {

     Order seckill(User user, GoodsVo goodsVo);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);
}
