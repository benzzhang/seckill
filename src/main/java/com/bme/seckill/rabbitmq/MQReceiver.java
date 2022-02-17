package com.bme.seckill.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bme.seckill.pojo.Goods;
import com.bme.seckill.pojo.SeckillMessage;
import com.bme.seckill.pojo.SeckillOrder;
import com.bme.seckill.pojo.User;
import com.bme.seckill.service.IGoodsService;
import com.bme.seckill.service.IOrderService;
import com.bme.seckill.service.ISeckillOrderService;
import com.bme.seckill.utils.JsonUtil;
import com.bme.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接受的消息："+message);

        SeckillMessage seckillMessage = JsonUtil.json2Object(message, SeckillMessage.class);

        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();

        // 判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1){
            return;
        }
        // 判断重复下单, MQ中用sql查询，不用缓存，保证真实数据查询
        // SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null){
            return;
        }

        // 下单
        orderService.seckill(user, goodsVo);
    }
}
