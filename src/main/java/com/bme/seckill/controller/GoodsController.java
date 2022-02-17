package com.bme.seckill.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.bme.seckill.pojo.User;
import com.bme.seckill.service.IGoodsService;
import com.bme.seckill.service.IUserService;
import com.bme.seckill.vo.DetailVo;
import com.bme.seckill.vo.GoodsVo;
import com.bme.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    // 跳转到商品列表页面
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user,
                         HttpServletRequest request, HttpServletResponse response){
        // 从redis中获取页面，如果不为空直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        //return "goodsList";
        // 页面为空时，手动渲染页面 ThymeleafViewResolver，存入redis缓存

        // Param-Map就是想要放在Thymeleaf模板引擎里的'数据'
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",context);
        if (!StringUtils.isEmpty(html)){
            // 失效时间
            valueOperations.set("goodsList",html,10, TimeUnit.SECONDS);
        }
        return html;
    }

    // 商品详情页面 —— 静态页面, Ajax请求接口 传输数据
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetial(User user, @PathVariable Long goodsId){

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = 0;
        //秒杀倒计时时间
        int remainSeconds = 0;
        //秒杀活动状态判断
        if (nowDate.before(startDate)){
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
            remainSeconds = 0;
        }

        // 用对象传参
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);

        //return "goodsDetail";
    }


    // 商品详情页面
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetial(Model model, User user, @PathVariable Long goodsId,
                           HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = 0;
        //秒杀倒计时时间
        int remainSeconds = 0;
        //秒杀活动状态判断
        if (nowDate.before(startDate)){
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("goods",goodsVo);
        model.addAttribute("endDate",endDate);
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",context);
        if (!StringUtils.isEmpty(html)){
            // 失效时间
            valueOperations.set("goodsDetail:" + goodsId,html,60, TimeUnit.SECONDS);
        }
        return html;

        //return "goodsDetail";
    }

}
