package com.itcast.dao;

import com.itcast.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    public void add(OrderSetting orderSetting);
    public long findCountByOrderDate(Date date);
    public void editNumberByOrderDate(OrderSetting orderSetting);
    public void editReservationsByOrderDate(OrderSetting orderSetting);
    public List<OrderSetting> getOrdersettingByMonth(Map map);
    public OrderSetting findByOrderDate(Date date);
}
