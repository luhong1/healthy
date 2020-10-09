package com.itcast.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itcast.dao.CheckitemDao;
import com.itcast.entity.PageResult;
import com.itcast.entity.QueryPageBean;
import com.itcast.pojo.CheckItem;
import com.itcast.service.CheckitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//检查项服务
@Service(interfaceClass = CheckitemService.class)
@Transactional
public class CheckitemServicrImpl implements CheckitemService {
    @Autowired    //注入DAO对象
    private CheckitemDao checkitemDao;


    public void add(CheckItem checkItem) {
     checkitemDao.add(checkItem);
    }

   //检查项分页查询
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currntPage=queryPageBean.getCurrentPage();
        Integer pageSize=queryPageBean.getPageSize();
        String queryString=queryPageBean.getQueryString();
        //完成分页查找，基于mybatis框架提供的分页助手插件来完成

       //相当于select * from t_checkitem limit 0,10   进行分页
        PageHelper.startPage(currntPage,pageSize);//分页助手，传递给页码、每页显示的条数自动对数据库的数据进行分页
        Page<CheckItem> page = checkitemDao.selectByCondition(queryString);//这两行代码之间不能写入其他东西写了也没用
        long total = page.getTotal();
        List<CheckItem> rows = page.getResult();
        return new PageResult(total,rows);
    }

    //根据ID删除检查项
    public void delteById(Integer id) {
      //判断当前检查项是否关联到检查组中
        long count=checkitemDao.findCountByCheckItemId(id);
        if(count>0){
            //当前检查项已经被关联不能删除
             new RuntimeException();
        }
        checkitemDao.deleteById(id);
    }


    public CheckItem findById(Integer id) {
        return checkitemDao.findById(id);
    }


    public void edit(CheckItem checkItem) {
        checkitemDao.edit(checkItem);
    }

    //查询所有检查项
    public List<CheckItem> findAll() {
        return checkitemDao.findAll();
    }


}
