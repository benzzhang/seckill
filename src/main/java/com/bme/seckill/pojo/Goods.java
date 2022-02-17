package com.bme.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author benzzhang
 * @since 2022-02-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String goodsName;

    /**
     * 标题
     */
    private String goodsTitle;

    /**
     * 图片
     */
    private String goodsImg;

    /**
     * 详情
     */
    private String goodsDetail;

    /**
     * 价格
     */
    private BigDecimal goodsPrice;

    /**
     * 库存,-1表示无限量
     */
    private Integer goodsStock;


}
