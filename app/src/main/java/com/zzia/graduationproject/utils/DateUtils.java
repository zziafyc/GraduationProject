package com.zzia.graduationproject.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fyc on 2017/5/15
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class DateUtils {
    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        String str = formatter.format(currentDate);
        return str;
    }
}
