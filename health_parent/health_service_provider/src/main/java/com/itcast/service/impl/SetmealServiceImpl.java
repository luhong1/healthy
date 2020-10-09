package com.itcast.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itcast.constant.RedisConstant;
import com.itcast.dao.SetmealDao;
import com.itcast.entity.PageResult;
import com.itcast.entity.QueryPageBean;
import com.itcast.pojo.Setmeal;
import com.itcast.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass  = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService{

    @Autowired  //注入DAO对象
    private SetmealDao setmealDao;
    @Autowired
    private JedisPool jedisPool;

    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
             setmealDao.add(setmeal);
             Integer setmealId=setmeal.getId();
             this.CheckGroupAndCheckItem(setmealId,checkgroupIds);
             //将图片名称保存到redis集合中
             jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
    }

    //查询所有套餐组
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage=queryPageBean.getCurrentPage();
        Integer pageSize=queryPageBean.getPageSize();
        String queryString=queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> setmealPage=setmealDao.selectByCondition(queryString);
        long total=setmealPage.getTotal();
        List<Setmeal> rows=setmealPage.getResult();
        return new PageResult(total,rows);
    }

    //查询某个套餐的数据
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    //根据套餐组查询出关联的检查组id
    public List<Integer> findfindSetmealIdsByCheckGroupIdById(Integer id) {
        return setmealDao.findfindSetmealIdsByCheckGroupIdById(id);
    }

    //更新套餐数据表
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        //1.对套餐的内容进行编辑
         setmealDao.edit(setmeal);
        //2.对关联对应检查组的检查项进行清除 清除表中t_setmeal_checkgroup的某个检查组中的id对应检查项
        setmealDao.deleteAssoication(setmeal.getId());
        //3.重新建立检查组和检查项的关联关系
        this.CheckGroupAndCheckItem(setmeal.getId(),checkgroupIds);
    }

    //对关联对应检查组的检查项进行清除 清除表中t_setmeal_checkgroup的某个检查组中的id对应检查项
    public void deleteAssoication(Integer id) {
        setmealDao.deleteAssoication(id);
    }

    //删除某个套餐
    public void delete(Integer id) {
        setmealDao.delete(id);
    }


    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }


    public Setmeal findByIdDetail(Integer id) {
        return setmealDao.findByIdDetail(id);
    }

    //查询套餐预约占比数据
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    public void CheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkgroupIds){
        if(checkgroupIds != null  && checkgroupIds.length>0){
            //下面是私有方法，执行速度很快
            Map<String,Object> map=new HashMap<>();
            map.put("setmealId",checkGroupId);
            map.put("checkgroupIds",checkgroupIds);
            setmealDao.setCheckGroupAndCheckItem(map);
            //下面是公有方法，如果数据太多会执行很慢
//            for(Integer ids : checkitemIds){
//                Map<String,Integer> map=new HashMap<>();
//                map.put("checkgroupId",integer);
//                map.put("checkitemId",ids);
//                checkGroupDao.setCheckGroupAndCheckItem(map);
//            }
        };
    }
}
