package com.itcast.service;

import com.itcast.entity.PageResult;
import com.itcast.entity.QueryPageBean;
import com.itcast.pojo.Setmeal;

import java.util.List;
import java.util.Map;


public interface SetmealService {
    public void add(Setmeal setmeal,Integer[] checkgroupIds);
    public PageResult findPage(QueryPageBean queryPageBean);
    public Setmeal findById(Integer id);
    public List<Integer> findfindSetmealIdsByCheckGroupIdById(Integer id);
    public void edit(Setmeal setmeal,Integer[] checkgroupIds);
    public void deleteAssoication(Integer id);
    public void delete(Integer id);
    public List<Setmeal> findAll();
    public Setmeal findByIdDetail(Integer id);
    public List<Map<String,Object>> findSetmealCount();
}
