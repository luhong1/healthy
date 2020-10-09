package com.itcast.dao;

import com.github.pagehelper.Page;
import com.itcast.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    public void add(Setmeal setmeal);
    public void setCheckGroupAndCheckItem(Map map);
    public Page<Setmeal> selectByCondition(String queryString);
    public Setmeal findById(Integer id);
    public List<Integer> findfindSetmealIdsByCheckGroupIdById(Integer id);
    public void edit(Setmeal setmeal);
    public void deleteAssoication(Integer id);
    public void delete(Integer id);
    public List<Setmeal> findAll();
    public Setmeal findByIdDetail(Integer id);
    public List<Map<String,Object>> findSetmealCount();
}
