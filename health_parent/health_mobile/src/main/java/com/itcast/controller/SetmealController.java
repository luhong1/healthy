package com.itcast.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.constant.MessageConstant;
import com.itcast.entity.Result;
import com.itcast.pojo.Setmeal;
import com.itcast.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    private SetmealService setmealService;

    @RequestMapping("/getAllSetmeal")
    public Result getAllSetmeal(){
        try {
            List<Setmeal> list = setmealService.findAll();
            return new Result(true,MessageConstant.GET_SETMEAL_LIST_SUCCESS,list);
        }catch (Exception e){
            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }
    //根据套餐ID查询套餐信息（套餐基本信息、套餐对应的检查组信息和检查组对应的检查项信息）
    @RequestMapping("/findByIdDetail")
    public Result findByIdDetail(Integer id){
        try {
            Setmeal setmal = setmealService.findByIdDetail(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmal);
        }catch (Exception e){
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    //根据套餐ID查询套餐信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Setmeal setmal = setmealService.findById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmal);
        }catch (Exception e){
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

}
