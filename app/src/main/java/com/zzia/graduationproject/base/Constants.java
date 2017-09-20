package com.zzia.graduationproject.base;

/**
 * Created by fyc on 2017/3/12
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class Constants {
    public static class RespCode {
        // 错误
        public static final int ERROR = 500;
        // 正确
        public static final int SUCCESS = 200;
        // 数据为空
        public static final int EMPTY = 400;
        // 数据已存在
        public static final int EXIST = 401;
        // 参数错误
        public static final int PARAMSERROR = 402;
        // 用户已在其他终端登录，在线中
        public static final int ONLINE = 403;
        // 获取缩略图失败
        public static final int THUMBERROR = 502;

    }

    public static class ThirdParty {
        public static final String MOB_APPKEY = "1c04a57b90a60";
        public static final String MOB_APPSECRET = "0d95c0ae80a2620ac7a14fa72678dbcc";
        public static final String QINIU_BUCKET="http://opx8c36ai.bkt.clouddn.com";
    }

    public static class Urls {
        //后台访问地址
        public static final String API_URL = "http://192.168.1.138:8080/GraduationProjectBackGround/";
        //public static final String API_URL = "http://47.94.152.253:8080/GraduationProjectBackGround/";
        //图片链接地址
        public static final String PIC_URL = "http://192.168.1.138:8080/GraduationProjectBackGround/";
       // public static final String PIC_URL = "http://47.94.152.253:8080/GraduationProjectBackGround/";
        //客户端存放根路径。
        public static final String CZF_ROOT_URL = "CZF/files/";
        //客户端缓存文件路径。下次启动【APP进程】时会删除所有该文件夹下所有文件。不要在该文件夹放需要长久的文件。
        public static final String CZF_TEMP_URL = CZF_ROOT_URL + "temp/";

    }

}
