# GraduationProject
> 这是一款从大学生“爱自拍，爱旅行”的实际出发，为了方便大学生记录校园生活点点滴滴，专为大学生定制的社交类app，
在这款app中，我们可以发表动态，定制DIY旅行计划，添加好友，聊天等。

#### 下面介绍一下该app用到的一些技术：

        1.网络请求框架retrofit2+rxjava
        2.2017年最新，抽屉式浮型侧滑菜单
        3.mob第三方手机验证
        4.zxing二维码扫描技术（参考github一品枫叶）
        5.cropview图片剪切技术
        6.coordinatorTabLayOut效果
        7.侧滑删除swipemenulistview
        8.百度地图POI地理编码和反地理编码
        9.MaterialDesign的cardview效果
        10.图片显示九宫格ninegridview
        11.依附于listview、recyclerview的floatingactionbutton
        12.张鸿洋的magic-viewpager和严正杰的viewPager定时器，完成3D轮播图的切换效果
        13.七牛云文件存储，图片视频都可以
        14.recyclerView加头部，recyclerviewheader2
#### 相关库的代码如下：
       dependencies {
           compile fileTree(include: ['*.jar'], dir: 'libs')
           androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
               exclude group: 'com.android.support', module: 'support-annotations'
           })
           //compile 'com.android.support:appcompat-v7:24.2.1'
           compile project(path: ':lib')
           testCompile 'junit:junit:4.12'
           //抽屉式浮型侧滑菜单
           compile 'com.rom4ek:arcnavigationview:1.0.2'
           //网络请求
           compile 'io.reactivex:rxjava:1.0.14'
           compile 'io.reactivex:rxandroid:1.0.1'
           compile 'com.squareup.retrofit2:retrofit:2.0.0-beta4'
           compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'
           compile 'com.squareup.retrofit2:adapter-rxjava:2.0.0-beta4'
           compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'
           //mob架包
           compile name: 'SMSSDK-2.1.3', ext: 'aar'
           compile name: 'SMSSDKGUI-2.1.3', ext: 'aar'
           //省市县三级联动、和时间选择器
           compile 'com.airsaid.library:pickerview:1.0.3'
           //二维码扫描库
           compile 'cn.yipianfengye.android:zxing-library:2.1'
           //图片剪切
           compile 'com.oginotihiro:cropview:1.0.0'
           //coordinatorTabLayOut效果
           compile 'cn.hugeterry.coordinatortablayout:coordinatortablayout:1.0.6'
           //侧滑删除效果
           compile 'com.baoyz.swipemenulistview:library:1.3.0'
           //多图选择器
           compile 'com.github.LuckSiege.PictureSelector:picture_library:v1.4.2'
           //百度地图
           compile files('libs/BaiduLBS_Android.jar')
           //carView的引用
           compile 'com.android.support:design:25.0.1'
           compile 'com.android.support:appcompat-v7:25.0.1'
           compile 'com.android.support:cardview-v7:25.0.1'
           //新版recyclerView，带刷新加载
           compile 'com.wuxiaolong.pullloadmorerecyclerview:library:1.1.2'
           //九宫格
           compile 'com.lzy.widget:ninegridview:0.2.0'
           //floatingActionButton的使用,依附于recyclerView
           compile 'com.melnykov:floatingactionbutton:1.3.0'
           //轮播图，两边留出一点的magicView（张鸿洋）
           compile 'com.zhy:magic-viewpager:1.0.1'
           //七牛云文件上传架包,java端
           compile 'com.qiniu:qiniu-java-sdk:7.2.+'
           //七牛云文件上传架包,Android端
           compile 'com.qiniu:qiniu-android-sdk:7.2.+'
           //recyclerView加头部
           compile 'com.bartoszlipinski:recyclerviewheader2:2.0.1'
           //添加腾讯Bugly错误日志检测
            compile 'com.tencent.bugly:crashreport:latest.release'
            compile 'com.tencent.bugly:nativecrashreport:latest.release'
           //引入litePal关系型sqLite数据库的封装
            compile 'org.litepal.android:core:1.6.0'
       }

> 最后，该项目的详细介绍和界面展示在我的博客里面可以看到，另外该项目会一直更新，如果发现一些bug，希望大家给我分享一下，我会及时改正。
博客地址：http://blog.csdn.net/u013769274/article/details/72822475
