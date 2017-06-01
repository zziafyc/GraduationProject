package com.shcyd.lib.utils.mediautils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by timor.fan on 2016/11/17.
 * *项目名：CZF
 * 类描述：
 */
public class MatchUtils {

    public static List<String> getUrlInContent(String content){
        List<String> result=new ArrayList<>();
        String regex = "([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher= pattern.matcher(content);
        while (matcher.find()){
            result.add(matcher.group());
        }
        Log.e("result",result.toString());
        return result;
    }

}
