package com.itcast.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itcast.dao.CheckGroupDao;
import com.itcast.entity.PageResult;
import com.itcast.entity.QueryPageBean;
import com.itcast.pojo.CheckGroup;
import com.itcast.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;



    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //增加信息到t_CheckGroup中    并关联数据中
        checkGroupDao.add(checkGroup);
        //获取最后存储进入t_CheckGroup中的id值
        Integer checkGroupId=checkGroup.getId();
        this.CheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }


    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage=queryPageBean.getCurrentPage();
        Integer pageSize=queryPageBean.getPageSize();
        String queryString=queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> page=checkGroupDao.selectByCondition(queryString);
        Long total=page.getTotal();
        List<CheckGroup> rows=page.getResult();
        return new PageResult(total,rows);
    }

    //根据检查组某个id查询进行数据回显
    public CheckGroup findById(Integer id) {
        CheckGroup checkGroup=checkGroupDao.findById(id);
        return checkGroup;
    }

    //根据检查组ID查询关联检查项的ID
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    //保存对检查组的编辑
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1.对检查组的内容进行编辑
       checkGroupDao.edit(checkGroup);
       //2.对关联对应检查组的检查项进行清除 清除表中t_checkitem_checkgroup的某个检查组中的id对应检查项
        this.deleteAssoication(checkGroup.getId());
        //3.重新建立检查组和检查项的关联关系
        Integer checkGroupId=checkGroup.getId();
        this.CheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    //删除检查组
    public void delteById(Integer id) {
        checkGroupDao.deleteById(id);
    }
    public void deleteAssoication(Integer id) {
        checkGroupDao.deleteAssoication(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    public void CheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds){
        if(checkitemIds != null  && checkitemIds.length>0){
            //下面是私有方法，执行速度很快
            Map<String,Object> map=new HashMap<>();
            map.put("checkgroupId",checkGroupId);
            map.put("checkitemIds",checkitemIds);
            checkGroupDao.setCheckGroupAndCheckItem(map);
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
