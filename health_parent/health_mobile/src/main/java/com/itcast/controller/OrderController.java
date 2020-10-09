package com.itcast.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.constant.MessageConstant;
import com.itcast.constant.RedisMessageConstant;
import com.itcast.entity.Result;
import com.itcast.pojo.Order;
import com.itcast.service.OrderService;
import com.itcast.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
        String telePhone = (String) map.get("telephone");
        //获取redis中的验证码
        String validateCodeInRedis = jedisPool.getResource().get(telePhone + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String) map.get("validateCode");
        //跟提交的验证码进行对比
         if(validateCodeInRedis!=null && validateCode!=null && validateCodeInRedis.equals(validateCode)){
             //对比成功，调用服务
             map.put("orderType", Order.ORDERTYPE_WEIXIN);//设置预约的类型   1.微信预约  2.电话预约
             Result result=null;
             try {
                  result = orderService.order(map);//通过Dubbo远程调用服务实现在线预约业务处理
             }catch (Exception e){
                 e.printStackTrace();
                 return result;
             }
             if(result.isFlag()){
                 try {
                     //表明预约成功，就可以向用户发送短信提示预约成功通知
                     SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE, telePhone, (String) map.get("orderDate"));
                 }catch (Exception e){
                     e.printStackTrace();
                 }
             }
             return result;
         }else {
             //对不不成功直接返回false
             return new Result(false, MessageConstant.VALIDATECODE_ERROR);
         }

    }
     //根据预约ID查询相关的信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try{
            Map map = orderService.findById(id);
            return new Result(false,MessageConstant.QUERY_ORDER_SUCCESS,map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
