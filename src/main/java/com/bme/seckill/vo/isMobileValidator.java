package com.bme.seckill.vo;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.bme.seckill.utils.ValidatorUtil;
import com.bme.seckill.validator.IsMobile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// 手机号码校验规则类
// 泛型<注解名，>
public class isMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if (required){
            return ValidatorUtil.isMoible(s);
        }else {
            if (StringUtils.isEmpty(s)){
                return true;
            }else {
                return ValidatorUtil.isMoible(s);
            }
        }
    }
}
