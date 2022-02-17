package com.bme.seckill.vo;

import com.bme.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends Goods {

    private BigDecimal seckillPrice;
    // 秒杀所用的活动库存
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
