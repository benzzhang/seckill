package com.bme.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bme.seckill.mapper.OrderMapper;
import com.bme.seckill.pojo.Order;
import com.bme.seckill.pojo.SeckillGoods;
import com.bme.seckill.pojo.SeckillOrder;
import com.bme.seckill.pojo.User;
import com.bme.seckill.service.IOrderService;
import com.bme.seckill.service.ISeckillGoodsService;
import com.bme.seckill.service.ISeckillOrderService;
import com.bme.seckill.utils.MD5util;
import com.bme.seckill.utils.UUIDUtil;
import com.bme.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author benzzhang
 * @since 2022-02-11
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    // 事务控制
    @Transactional
    @Override
    // 秒杀功能
    public Order seckill(User user, GoodsVo goodsVo) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));

        // 库存>0才售出，解决超卖： 事务： 先减少库存 + 判断 stock_count > 0
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql(
                        "stock_count = stock_count-1").eq(
                        "goods_id",goodsVo.getId()).gt("stock_count",0));

        if (seckillGoods.getStockCount() < 1){
            valueOperations.set("isStockEmpty:"+goodsVo.getId(), 0);
            return null;
        }

        //  seckillGoodsService.updateById(seckillGoods);

        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsNamet(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannle(1L);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);

        redisTemplate.opsForValue().set("order:"+user.getId()+":"+ goodsVo.getId(),seckillOrder);
        return order;
    }

    @Override
    // 生成秒杀地址
    public String createPath(User user, Long goodsId) {

        String str = MD5util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str, 60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    // 校验秒杀地址
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId<0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);

        return path.equals(redisPath);

    }
}
