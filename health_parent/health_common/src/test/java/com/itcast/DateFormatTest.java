package com.itcast;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : NieXin
 * @version 1.0
 * @date 2020/8/27 0027 10:43
 * 时间和字符串类型的相互转换
 * 把时间转换成字符串类型   把字符串转换为时间类型数据
 */
public class DateFormatTest {
    public static void main(String[] args) {
        Date date =  new Date();
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String format1 = simpleDateFormat.format(date);
        System.out.println(format1);
    }
    @Test
    public  void test() throws Exception{
        String data = "2020-08-27 10:46:49";
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(data);
        System.out.println(date.getTime());
    }
}
