package com.itcast.dao;

import com.github.pagehelper.Page;
import com.itcast.pojo.CheckItem;

import java.util.List;

public interface CheckitemDao {
    public void add(CheckItem checkItem);

    //必须是Page  他是跟pagehelper对应的，一起使用的
    public Page<CheckItem> selectByCondition(String queyString);

    public long findCountByCheckItemId(Integer id);

    public void deleteById(Integer id);

    public CheckItem findById(Integer id);

    public void edit(CheckItem checkItem);

    public List<CheckItem> findAll();
}
