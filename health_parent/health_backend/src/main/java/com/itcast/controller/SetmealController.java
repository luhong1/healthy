package com.itcast.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.constant.MessageConstant;
import com.itcast.constant.RedisConstant;
import com.itcast.entity.PageResult;
import com.itcast.entity.QueryPageBean;
import com.itcast.entity.Result;
import com.itcast.pojo.Setmeal;
import com.itcast.service.SetmealService;
import com.itcast.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

//套餐管理
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference   //去服务器中心查找服务    要用alibaba 的包  不用jdk的
    private SetmealService setmealService;

    //往数据库中的套餐表增加数据
    @PreAuthorize("hasAuthority('SETMEAL_ADD')")//权限校验
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        try {
            setmealService.add(setmeal, checkgroupIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    //更新数据库中的套餐表数据
    @PreAuthorize("hasAuthority('SETMEAL_EDIT')")//权限校验
    @RequestMapping("/edit")
    public Result edit(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        try {
            setmealService.edit(setmeal, checkgroupIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);
    }

    //分页查询
    @PreAuthorize("hasAuthority('SETMEAL_QUERY')")//权限校验
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult=setmealService.findPage(queryPageBean);
        return pageResult;
    }
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
           Setmeal setmeal=setmealService.findById(id);
            return new Result(true,MessageConstant.QUERY_SETMEALLIST_SUCCESS,setmeal);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }
    @RequestMapping("/findSetmealIdsByCheckGroupId")
    public Result findSetmealIdsByCheckGroupId(Integer id){
        try {
           List<Integer> list=setmealService.findfindSetmealIdsByCheckGroupIdById(id);
            return new Result(true,MessageConstant.QUERY_SETMEALLIST_SUCCESS,list);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    @RequestMapping("/deleteAssoication")
    public Result deleteAssoication(Integer id){
        try {
          setmealService.deleteAssoication(id);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.DELETE_SETMEAL_SUCCESS);
    }

    @PreAuthorize("hasAuthority('SETMEAL_DELETE')")//权限校验
    @RequestMapping("/delete")
    public Result delete(Integer id){
        try {
            setmealService.delete(id);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.DELETE_SETMEAL_SUCCESS);
    }

    //使用JedisPool来操作redis服务
    @Autowired
    private JedisPool jedisPool;

    //文件上传   RequestParam:获取前端的值
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        String originalFileName=imgFile.getOriginalFilename();//原始文件名
        int index=originalFileName.lastIndexOf(".");
        String extention=originalFileName.substring(index-1);//截取原始文件的后缀
        String fileName= UUID.randomUUID().toString()+extention;// UUID.randomUUID().toString()是javaJDK提供的一个自动生成主键的方法，产生唯一识别码
        try {
            //调用七牛云服务器，上传文件
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            //将上传图片名称存入Redis，基于Redis的Set集合存储   第一个参数是保存数据库的库名  第二个参数是要保存的数据
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
    }

}
