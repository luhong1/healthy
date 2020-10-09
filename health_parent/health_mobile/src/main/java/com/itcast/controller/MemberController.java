package com.itcast.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itcast.constant.MessageConstant;
import com.itcast.constant.RedisMessageConstant;
import com.itcast.entity.Result;
import com.itcast.pojo.Member;
import com.itcast.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
//处理会员相关的操作
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;
/*
    1、校验用户输入的短信验证码是否正确，如果验证码错误则登录失败
    2、如果验证码正确，则判断当前用户是否为会员，如果不是会员则自动完成会员注册
    3、向客户端写入Cookie，内容为用户手机号
    4、将会员信息保存到Redis，使用手机号作为key，保存时长为30分钟*/
    @RequestMapping("/login")
    //写入Cookie就是通过response写入数据到前端
    public com.itcast.entity.Result login(HttpServletResponse response,@RequestBody Map map){
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if(validateCode.equals(validateCodeInRedis) && validateCode!=null && validateCodeInRedis!=null){
            //验证码输入正确
            //判断当前用户是否为会员
            Member member = memberService.findByTelephone(telephone);
            if (member==null){
                //不是会员，则自动完成注册，把注册信息保存在会员表
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                memberService.add(member);
            }
            //向客户端浏览器写入cookie，内容为手机号   -----跟踪用户的一个手段
            Cookie cookie=new Cookie("login_member_telephone",telephone);
            cookie.setPath("/");//路径
            cookie.setMaxAge(60*60*24*30); //有效期30天
            response.addCookie(cookie);
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone,1800,json);
            return new Result(true,MessageConstant.LOGIN_SUCCESS);
        }else{
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }

    }
}
