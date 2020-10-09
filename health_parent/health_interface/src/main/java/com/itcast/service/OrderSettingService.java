package com.itcast.service;

import com.itcast.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    public void add(List<OrderSetting> orderSetting);
    public List<Map> getOrdersettingByMonth(Integer year,Integer month);
    public void editNumberByDate(OrderSetting orderSetting);
}
