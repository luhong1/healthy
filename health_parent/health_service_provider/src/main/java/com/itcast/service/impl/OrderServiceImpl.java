package com.itcast.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.constant.MessageConstant;
import com.itcast.dao.MemberDao;
import com.itcast.dao.OrderDao;
import com.itcast.dao.OrderSettingDao;
import com.itcast.entity.Result;
import com.itcast.pojo.Member;
import com.itcast.pojo.Order;
import com.itcast.pojo.OrderSetting;
import com.itcast.service.OrderService;
import com.itcast.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

//体检预约服务
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;
    @Override
    public Result order(Map map) throws Exception {
       // 1、检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行预约
        String orderDate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);
        OrderSetting byOrderDate = orderSettingDao.findByOrderDate(date);
        if(byOrderDate==null){
            //指定日期没有进行预约设置，则无法完成体检预约
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //2、检查用户所选择的预约日期是否已经约满，如果已经约满则无法预约
        if(byOrderDate.getNumber()<=byOrderDate.getReservations()){
            //已经预约满了，无法在进行预约
            return new Result(false,MessageConstant.ORDER_FULL);
        }
       // 3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
        String telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        if(member!=null){
            //获取用户的id   来进行判断是否在重复预约
            Integer memberId = member.getId();
            String setmealId = (String) map.get("setmealId");//套餐ID
            Order order = new Order(memberId, date, Integer.parseInt(setmealId));
            //根据条件查询
            List<Order> list = orderDao.findByCondition(order);
            if(list!=null && list.size()>0){
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }else{
            // 4、检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
             member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            //加入数据库中
            memberDao.add(member);
        }
        //5、预约成功，更新当日的已预约人数
        Order order=new Order();
        order.setMemberId(member.getId());//预约会员的用户ID
        order.setOrderDate(date);//预约时间
        order.setOrderType((String) map.get("orderType"));//预约类型
        order.setOrderStatus(Order.ORDERSTATUS_NO);//到诊状态
        order.setSetmealId(Integer.parseInt((String) map.get("setmealId")));//套餐ID
        orderDao.add(order);

       byOrderDate.setReservations(byOrderDate.getReservations()+1);
       orderSettingDao.editReservationsByOrderDate(byOrderDate);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

   //根据预约ID查询预约信息
    public Map findById(Integer id) {
        Map map = orderDao.findById4Detail(id);
        if(map != null) {
            try {
                //处理日期格式
                Date orderDate = (Date) map.get("orderDate");
                map.put("orderDate", DateUtils.parseDate2String(orderDate));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }
}
