package com.itcast.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.constant.MessageConstant;
import com.itcast.entity.Result;
import com.itcast.pojo.OrderSetting;
import com.itcast.service.OrderSettingService;
import com.itcast.utils.POIUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;

    /*** Excel文件上传，并解析文件内容保存到数据库
     *  * @param excelFile
     *  * @return */
    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile")MultipartFile excelFile){
        try {
            List<String[]> list= POIUtils.readExcel(excelFile);//使用POI解析表格数据
            List<OrderSetting> data=new ArrayList<>();
            for(String[] strings:list){
                String orderDate = strings[0];
                String number = strings[1];
               //把字符串转换成日期类型 new Date(orderDate)    把字符串转换成整数Integer.parseInt(number)
                OrderSetting orderSetting = new OrderSetting(new Date(orderDate),Integer.parseInt(number));
                data.add(orderSetting);
            }
            //通过dubbo调用服务实现数据的批量导入
            orderSettingService.add(data);
        }catch (Exception e){
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
        return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
    }

    @RequestMapping("/getOrdersettingByMonth")
    public Result getOrdersettingByMonth(Integer year,Integer month) {
        try {
            List<Map> list = orderSettingService.getOrdersettingByMonth(year,month);
            return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS, list);
        } catch (Exception e) {
            return new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }
        @PreAuthorize("hasAuthority('ORDERSETTING')")//权限校验
        @RequestMapping("/editNumberByDate")
        public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
            try {
                 orderSettingService.editNumberByDate(orderSetting);
            }catch (Exception e){
                return new Result(false, MessageConstant.ORDERSETTING_FAIL);
            }
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
    }
}
