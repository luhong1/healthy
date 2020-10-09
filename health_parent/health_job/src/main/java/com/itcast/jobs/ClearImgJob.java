package com.itcast.jobs;

import com.itcast.constant.RedisConstant;
import com.itcast.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/*自定义Job，定时清理垃圾*/
public class ClearImgJob {
    @Autowired
    private JedisPool jedisPool;

    public void clearImg(){
       //根据Redis中保存的集合，来进行差值计算，获取垃圾图片的名称集合
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if(set!=null){
            for(String fileName:set){
                //删除七牛云中的图片名称
                QiniuUtils.deleteFileFromQiniu(fileName);
                //从Redis集合中删除图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
               System.out.println("自定义任务执行，清理垃圾图片成功"+fileName);
            }
        }
    }

}
