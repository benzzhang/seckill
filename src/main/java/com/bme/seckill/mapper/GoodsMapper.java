package com.bme.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bme.seckill.pojo.Goods;
import com.bme.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author benzzhang
 * @since 2022-02-11
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    // 获取商品列表
    List<GoodsVo> findGoodsVo();

    //获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
