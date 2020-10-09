package com.itcast.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.constant.MessageConstant;
import com.itcast.entity.PageResult;
import com.itcast.entity.QueryPageBean;
import com.itcast.entity.Result;
import com.itcast.pojo.CheckItem;
import com.itcast.service.CheckitemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//检查项管理
@RestController
@RequestMapping("/checkitem")
public class CheckitemController {

    @Reference  //去服务器中心查找服务    要用alibaba 的包  不用jdk的
    private CheckitemService checkitemService;


    //在服务器上时用logger来输出信息   输出日志信息  方便查看日志  在工作时一般是用这个方法
    private final Logger logger = LoggerFactory.getLogger(CheckitemController.class);


    //新增检查项   RequestBody解析前端的json数据然后封装成指定的对象
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")//权限校验
    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem) {
        try {
            checkitemService.add(checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加信息报错", e);
            //服务调用失败
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }

        return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
    }

    //检查项分页查询
    @PreAuthorize("hasAuthority('CHECKITEM_QUERY')")//权限校验
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = checkitemService.pageQuery(queryPageBean);
        //由于加了RestController注解，这样spingmvc框架就会自动把pageResult对象转换成json数据返回去。
        return pageResult;
    }

    //删除检查项
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")//权限校验
    @RequestMapping("/delete")
    public Result delete(Integer id) {
        try {
            checkitemService.delteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除信息报错", e);
            //服务调用失败
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }

        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }

    //数据回显
    @GetMapping("/findById")
    public Result findById( Integer id) {
        try {
           CheckItem checkItem=checkitemService.findById(id);
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除信息报错", e);
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    //修改检查项
    @PreAuthorize("hasAuthority('CHECKITEM_EDIT')")//权限校验
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem) {
        try {
            checkitemService.edit(checkItem);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加信息报错", e);
            //服务调用失败
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }

        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    //查询所有检查组
    @RequestMapping("/findAll")
    public Result findAll() {
        try {
            List<CheckItem> list=checkitemService.findAll();
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除信息报错", e);
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

}
