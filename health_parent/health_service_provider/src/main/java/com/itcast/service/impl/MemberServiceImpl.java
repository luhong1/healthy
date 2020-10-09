package com.itcast.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itcast.dao.MemberDao;
import com.itcast.pojo.Member;
import com.itcast.service.MemberService;
import com.itcast.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//会员服务
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;
    //查找是否存在该会员
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    //添加会员信息
    public void add(Member member) {
        String password = member.getPassword();
        if(password!=null){
            //使用md5将明文密码进行加密
            password=MD5Utils.md5(password);
            member.setPassword(password);
        }

        memberDao.add(member);
    }

    //根据月份查询会员数量
    public List<Integer> findMemberCountByMonths(List<String> months) {  //2020.10
        List<Integer> list=new ArrayList<>();
        for (String month : months) {
            String time=month+".31";
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
             df.setLenient(false);
            Date date= null;
            try {
                date = df.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Integer memberCount = memberDao.findMemberCountBeforeDate(date);
            list.add(memberCount);
        }
        return list;
    }
}
