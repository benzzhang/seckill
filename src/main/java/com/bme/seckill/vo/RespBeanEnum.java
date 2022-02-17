package com.bme.seckill.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    SESSION_ERROOR(500210,"session不存在或已失效"),
    USER_ERROR(500210,"用户不存在"),
    LOGIN_ERROR(500211,"用户名或者密码错误"),
    MOBILE_ERROR(500212,"手机号码格式错误"),
    BIND_ERROR(500213,"参数校验异常"),
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"商品限购1件"),
    REQUEST_ILLEGAL(500502,"请求非法");


    private final Integer code;
    private final String message;

}
