package com.bme.seckill.controller;

import com.bme.seckill.pojo.Order;
import com.bme.seckill.pojo.SeckillMessage;
import com.bme.seckill.pojo.SeckillOrder;
import com.bme.seckill.pojo.User;
import com.bme.seckill.rabbitmq.MQSender;
import com.bme.seckill.service.IGoodsService;
import com.bme.seckill.service.IOrderService;
import com.bme.seckill.service.ISeckillOrderService;
import com.bme.seckill.utils.JsonUtil;
import com.bme.seckill.vo.GoodsVo;
import com.bme.seckill.vo.RespBean;
import com.bme.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/secKill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping(value = "/doSecKill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROOR);
        }

        // 在redis中进行库存预减操作，需要确保: 之前的所有订单在redis中都有记录保存,否则会认为用户未进行过下单，导致redis库存减少，数据库不变。

        ValueOperations valueOperations = redisTemplate.opsForValue();

/*
        // 秒杀地址校验
        boolean check = orderService.checkPath(user, goodsId, path);

        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }*/

        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);

/*
        // 使用Map来标记的话，会造成redis库存变为负数
        if (EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
*/

        if (stock < 0){
            EmptyStockMap.put(goodsId, true);
            log.info("进入if");
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage seckillMessage = new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);

/*
        model.addAttribute("user", user);
        // 进行秒杀时频繁进行数据库查询，优化：Redis预减库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        // 库存判断
        if (goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.SESSION_ERROOR);
        }

        // 判断是否重复下单

        *//* service用数据库查询 *//*
        //  SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
        //         user.getId()).eq("goods_id", goodsId));

        *//* redis查询-更快 *//*
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);

        if (seckillOrder != null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user,goodsVo);
        model.addAttribute("order",order);
        model.addAttribute("goods",goodsVo);
        return "orderDetail";
        return null;*/
    }

    @Override
    // 初始化，加载商品库存
    // 初始化bean的时候都会执行该方法; implements InitializingBean 重写
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            log.info("预加载"+goodsVo.getId()+":"+goodsVo.getStockCount());
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            // 对库存进行标记的Map，减少查询库存的sql操作
            // 在并发情况下，这个方法不能用：大量请求将redis中的stock减为了负数，其他请求便不会再进入到 valueOperations.increment
            // 导致数据库中库存(>0)和redis(<0)不一致
            EmptyStockMap.put(goodsVo.getId(),false);
        }
        );
    }

    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user,Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROOR);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }


    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROOR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }
}
