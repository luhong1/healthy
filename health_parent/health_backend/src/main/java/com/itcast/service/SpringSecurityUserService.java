package com.itcast.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itcast.pojo.Permission;
import com.itcast.pojo.Role;
import com.itcast.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {
    //使用dubbo远程调用服务提供方来获取数据库中的用户信息
    @Reference
    private UserService userService;

    //根据用户名查询数据库来查询用户信息
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if(user==null){
            //用户名不存在
            return null;
        }
        List<GrantedAuthority> list=new ArrayList<>();
        //动态为当前用户授权
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            //遍历角色集合，为用户授予角色
            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            Set<Permission> permissions = role.getPermissions();
            //遍历权限集合，为用户授权
            for (Permission permission : permissions) {
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }

        }
        org.springframework.security.core.userdetails.User securityUser=new org.springframework.security.core.userdetails.User(username,user.getPassword(),list);
        return securityUser;
    }
}
