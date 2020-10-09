package com.itcast.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itcast.constant.MessageConstant;
import com.itcast.constant.RedisMessageConstant;
import com.itcast.entity.Result;
import com.itcast.utils.SMSUtils;
import com.itcast.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

//验证码的发送
@RestController
@RequestMapping("/sendValidateCode")
public class ValidateCode {

    @Autowired
    private JedisPool jedisPool;
     //用户在线体检预约发送验证码
    @RequestMapping("/send4order")
    public Result send4order(String telPhone) {
       //给用户发送验证码
        Integer  validateCode4= ValidateCodeUtils.generateValidateCode(4);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telPhone,validateCode4.toString());
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //将验证码保存到redis中（保存5分钟）   setex中的第二个参数是缓存的时间，单位为s
        jedisPool.getResource().setex(telPhone+ RedisMessageConstant.SENDTYPE_ORDER,300,validateCode4.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS,validateCode4);
    }
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone) {
        //给用户发送验证码
        Integer  validateCode4= ValidateCodeUtils.generateValidateCode(6);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode4.toString());
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //将验证码保存到redis中（保存5分钟）   setex中的第二个参数是缓存的时间，单位为s
        jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,300,validateCode4.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS,validateCode4);
    }
}
