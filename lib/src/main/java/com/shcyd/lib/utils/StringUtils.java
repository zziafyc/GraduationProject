package com.shcyd.lib.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-11-02
 * @time 14:31
 * @changlog
 * @fixme
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        if (str == null
                || str.length() == 0
                || str.equalsIgnoreCase("null")
                || str.isEmpty()
                || str.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCaptcha(String verifyCode) {
        return verifyCode != null && verifyCode.matches("^\\d{4}$");
    }

    public static boolean noEmptyList(List list) {
        if (list == null || list.isEmpty() || list.size() == 0) return false;
        return true;
    }
    public static boolean isNull(Object... obj) {
        for (Object o : obj) {
            if (o == null) return true;
        }
        return false;
    }

    public static boolean isNull2(Object... obj) {
        for (Object o : obj) {
            if (o == null || o.equals("")) return true;
        }
        return false;
    }

    public static String arrayToString(String[] array) {
        if (array != null) {
            StringBuffer sb = new StringBuffer();
            for (String s : array) {
                sb.append(s);
            }
            return sb.toString();
        }
        return "";
    }


    public static void setWordColor(String string, int start, int end, TextView view, int colorStateList) {

        //设置局部字体颜色变化
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(colorStateList);
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(builder);
    }

    public static SpannableStringBuilder getWordColor(String string, int start, int end, int colorStateList) {

        //设置局部字体颜色变化
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(colorStateList);
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * @param dbDateTime:格式：2016-10-12 13:55:44
     * @return 客户端需要显示的时间格式
     */
    public static String getShowDateTime(String dbDateTime) {
        if (dbDateTime == null || dbDateTime.isEmpty()) {
            return "";
        }
        return getTimeByCurrentTime(dbDateTime);
    }

    // 注意时间格式yyyy-MM-dd HH:mm:ss
    public static String getTimeByCurrentTime(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        Date now = new Date();
        try {
            d = format.parse(date);
            if (now.getYear() - d.getYear() > 0) {
                SimpleDateFormat yFormat = new SimpleDateFormat(
                        "yyyy/MM/dd HH:mm");
                return yFormat.format(d);
            } else if ((now.getMonth() - d.getMonth() > 0) || (now.getDate() - d.getDate() > 1)) {
                SimpleDateFormat dFormat = new SimpleDateFormat("MM/dd HH:mm");
                return dFormat.format(d);
            } else if (now.getDate() - d.getDate() > 0) {
                return "昨天  " + date.substring(11, 16);
            } else {
                long delta = (now.getTime() - d.getTime()) / 1000;
                if (delta / (60 * 60) > 0)
                    return delta / (60 * 60) + "小时前";
                if (delta / (60) > 0)
                    return delta / (60) + "分钟前";
                return "刚刚";
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;

    }

    /**
     * TODO:该方法用于判断是否显示时间（类似于公众号消息上面的时间）。判断依据：发布时间和当前时间的时间差，来决定是否显示。业务逻辑还没写。
     *
     * @param dbDateTime:格式：2016-10-12 13:55:44
     * @return true or false
     */
    public static boolean isShowDateTime(String dbDateTime) {
        if (dbDateTime == null || dbDateTime.isEmpty()) {
            return false;
        }
        //// FIXME: 2016/10/12 在这个写业务逻辑
        return true;
    }

    //获取dateTime的date:2016-10-12 13:55:44
    public static String getSubDate(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "";
        }
        dateTime = dateTime.substring(0, dateTime.indexOf(" "));
        return dateTime;
    }

    public static void main(String[] args) {
        getSubDate("2016-10-12 13:55:44");
    }

    //TODO:需要深度测试
    public static synchronized String getMs2Minuts(long ms) {
        String reslut = "";
        long sceond = ms / 1000;
        //可能会出现0
        if (sceond == 0) return "00:01";
        //如果小于一分钟
        if (sceond / 60 < 1) {
            //如果小于10s
            if (sceond / 10 < 1) {
                return "00:0" + sceond;
            } else {
                return "00:" + sceond;
            }
        } else {
            //太大，不合法数据
            if (sceond / 60 >= 60) {
                return "99:99";
            } else {
                //分钟数小于10
                if (sceond / 60 < 10)
                    reslut += "0" + sceond / 60;
                else reslut += sceond / 60;

                //s小于10
                if (sceond % 60 < 10)
                    reslut += ":0" + sceond % 60;
                else
                    reslut += ":" + sceond % 60;

                return reslut;
            }
        }
    }
}
