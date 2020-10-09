package com.itcast.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.dao.PermissionDao;
import com.itcast.dao.RoleDao;
import com.itcast.dao.UserDao;
import com.itcast.pojo.Permission;
import com.itcast.pojo.Role;
import com.itcast.pojo.User;
import com.itcast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;
    //根据用户名查询数据库获取用户信息和关联的角色信息，同时需要查询角色信息关联的权限信息
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);
        if (user==null){
            return null;
        }
        Integer userId = user.getId();
        //根据用户ID查询对应的角色
        Set<Role> roles = roleDao.findByUserId(userId);
        for (Role role : roles) {
            Integer roleId = role.getId();
            //根据角色查询关联的权限
            Set<Permission> permissions = permissionDao.findByRoleId(roleId);
            role.setPermissions(permissions);//让角色关联权限
        }
        user.setRoles(roles);//让用户关联角色
        return user;
    }
}
