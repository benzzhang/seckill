package com.bme.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5util {

    private static final String salt="1a2b3c4d";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassToFormPass(String inputPass){
        String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt){
        String str = ""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt){
        String formPass = inputPassToFormPass(inputPass);
        return formPassToDBPass(formPass,salt);
    }

    public static void main(String[] args) {
        // 298516350807039dbe2d0ffcaffc7cd8
        System.out.println(inputPassToFormPass("123456"));
        // 897e59c19347d11e3828685699268d00
        System.out.println(formPassToDBPass("298516350807039dbe2d0ffcaffc7cd8","1a2b3c4d"));
        // 897e59c19347d11e3828685699268d00
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));

    }
}
