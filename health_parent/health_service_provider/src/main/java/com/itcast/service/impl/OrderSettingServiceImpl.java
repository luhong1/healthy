package com.itcast.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.dao.OrderSettingDao;
import com.itcast.pojo.OrderSetting;
import com.itcast.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired   //注入DAO对象
    private OrderSettingDao orderSettingDao;

    //批量导入预约数据
    public void add(List<OrderSetting> orderSetting) {
        if(orderSetting!=null && orderSetting.size()>0){
            for(OrderSetting orderSetting1:orderSetting) {
                //检查此数据（日期）是否存在
                long count = orderSettingDao.findCountByOrderDate(orderSetting1.getOrderDate());
                if(count > 0){
                    //已经存在，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting1);
                }else{
                    //不存在，执行添加操作
                    orderSettingDao.add(orderSetting1);}
            }
        }
    }

    //根据日期查询预约设置数据
    public List<Map> getOrdersettingByMonth(Integer year,Integer month) {
         String dateYear=String.valueOf(year);  //2020-8-1
         String dateMonth=String.valueOf(month);   //2020-8-31
        Map<String,String> map=new HashMap<>();
        map.put("dateYear",dateYear);
        map.put("dateMonth",dateMonth);
        List<OrderSetting> list = orderSettingDao.getOrdersettingByMonth(map);
        List<Map> result=new ArrayList<>();
        if(list.size()>0 && list!=null){
            for(OrderSetting orderSetting:list){
                Map<String,Object> m=new HashMap<>();
                m.put("date",orderSetting.getOrderDate().getDate());//日期号
                m.put("number",orderSetting.getNumber());
                m.put("reservations",orderSetting.getReservations());
                result.add(m);
            }
        }

        return result;
    }

    //预约人数的变化
    public void editNumberByDate(OrderSetting orderSetting) {
        //检查此数据（日期）是否存在
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if(count > 0){
            //已经存在，执行更新操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            //不存在，执行添加操作
            orderSettingDao.add(orderSetting);}
    }
}
