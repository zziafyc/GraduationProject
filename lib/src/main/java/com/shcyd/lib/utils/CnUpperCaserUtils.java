package com.shcyd.lib.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wgyscsf on 2016/7/20.
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class CnUpperCaserUtils {
    // 整数部分
    private String integerPart;
    // 小数部分
    private String floatPart;

    // 将数字转化为汉字的数组,因为各个实例都要使用所以设为静态
    private static final char[] cnNumbers = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'};

    // 供分级转化的数组,因为各个实例都要使用所以设为静态
    private static final char[] series = {'元', '拾', '百', '仟', '万', '拾', '百', '仟', '亿'};

    /**
     * 构造函数,通过它将阿拉伯数字形式的字符串传入
     *
     * @param original
     */
    public CnUpperCaserUtils(String original) {
        // 成员变量初始化
        integerPart = "";
        floatPart = "";

        if (original.contains(".")) {
            // 如果包含小数点
            int dotIndex = original.indexOf(".");
            integerPart = original.substring(0, dotIndex);
            floatPart = original.substring(dotIndex + 1);
        } else {
            // 不包含小数点
            integerPart = original;
        }
    }

    /**
     * 取得大写形式的字符串
     *
     * @return
     */
    public String getCnString() {
        // 因为是累加所以用StringBuffer
        StringBuffer sb = new StringBuffer();

        // 整数部分处理
        for (int i = 0; i < integerPart.length(); i++) {
            int number = getNumber(integerPart.charAt(i));
            sb.append(cnNumbers[number]);
            sb.append(series[integerPart.length() - 1 - i]);
        }

        // 小数部分处理
        if (floatPart.length() > 0) {
            sb.append("点");
            for (int i = 0; i < floatPart.length(); i++) {
                int number = getNumber(floatPart.charAt(i));

                sb.append(cnNumbers[number]);
            }
        }

        // 返回拼接好的字符串
        return sb.toString();
    }

    /**
     * 将字符形式的数字转化为整形数字
     * 因为所有实例都要用到所以用静态修饰
     *
     * @param c
     * @return
     */
    private static int getNumber(char c) {
        String str = String.valueOf(c);
        return Integer.parseInt(str);
    }

    /**
     * 数字金额大写转换，思想先写个完整的然后将如零拾替换成零
     * 要用到正则表达式
     */
    public static String digitUppercase(double n){

        String fraction[] = {"角", "分"};
        String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        String unit[][] = {{"元", "万", "亿"},                      {"", "拾", "佰", "仟"}};
        String head = n < 0? "负": "";
        n = Math.abs(n);
        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int)(Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if(s.length()<1){
            s = "整";
        }
        int integerPart = (int)Math.floor(n);
        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p ="";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart%10]+unit[1][j] + p;
                integerPart = integerPart/10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }

    public static String getEnglishNums(String num) {
        Pattern p = Pattern.compile("(?<=\\d)(?=(\\d\\d\\d)+$)");
        Matcher m = p.matcher(num);
        return m.replaceAll(",");
    }
}
