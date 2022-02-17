package com.bme.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bme.seckill.pojo.Goods;
import com.bme.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author benzzhang
 * @since 2022-02-11
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
