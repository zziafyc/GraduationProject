package com.zzia.graduationproject.qiniu;



import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * Created by fyc on 2017/5/13
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public class QiNiuFileUpload {
    //七牛云的AK
    public static final String accessKey = "9oHoHvfN-Mw_Mdq6RJSKKdHgqr-Rvxl0aayhyKYv";
    //七牛云的SK
    public static final String secretKey = "tUKf7LOvNnTyAYqC30WYj3xQgtSwMHjnDtd9z6dU";
    //空间名称
    public static final String bucket = "graduationprojectbucket";
    //时间
    static long  expireSeconds = 3600;

    private static String token;

    public static String getToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        StringMap putPolicy = new StringMap();
        token = auth.uploadToken(bucket, null, expireSeconds, putPolicy);
        if (token != null) {
            return token;
        }
        return null;
    }
}
