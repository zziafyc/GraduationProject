package com.shcyd.lib.utils.mediautils;

import android.app.Activity;
import android.os.Bundle;

import java.util.List;

/**
 * Created by wgyscsf on 2016/7/25.
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */
public class Provider extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.main);
        // 音频
        AbstructProvider provider = new AudioProvider(this);
        List<?> list = provider.getList();
        // 视频
        // AbstructProvider provider = new VideoProvider(this);
        // List<?> list = provider.getList();
        // 图片
        // AbstructProvider provider = new ImageProvider(this);
        // List<?> list = provider.getList();
    }

}


