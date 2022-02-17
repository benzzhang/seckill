package com.bme.seckill.service;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bme.seckill.pojo.User;
import com.bme.seckill.vo.LoginVo;
import com.bme.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author benzzhang
 * @since 2022-01-25
 */
public interface IUserService extends IService<User> {

    // 登录接口
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
