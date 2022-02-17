package com.bme.seckill.controller;


import com.bme.seckill.pojo.User;
import com.bme.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author benzzhang
 * @since 2022-01-25
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    // 用户信息（test）
    public RespBean info(User user){
        return RespBean.success(user);
    }

}
