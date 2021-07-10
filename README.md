### 前言： “校园微生活”是本着以“爱生活，爱分享，爱旅游”为主题，方便大家记录大学生活点点滴滴的一款校园社交类软件。每个人的大学都只有一次，而这一次，又有谁不想刻骨铭心啊。在这个信息爆炸的时代里，自拍，秀恩爱，生日party秀，旅行秀....这些成了大家习以为常的事情，用户会随时随地拿出自己的的手机，自信地拍一张秀一下，于是，生活无处不风景。本设计的任务就是基于Android平台设计一款校园社交类app——基于Android的“校园微生活”。
### 一：App端整体架构
采用普通的MVC模式分模块搭建，MVC + Rxjava + Retrofit
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710141422715.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)
### 二：App端功能模块介绍
1. 用户注册：通过填写手机号，输入正确手机号验证码完成手机号注册。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710142558508.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)

2. 用户登录：用户可以通过三方qq、微信进行登录。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710154601739.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)

3. app主页：展示四个模块，微录、微游、微聊、我的。主页显示聊天信息。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021071014275869.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)
4. 发布动态。用户可在“微录”模块，点击好友动态，可以查看好友发布的动态，同时可以点击右上角的图标，发布图片、或者是视频动态。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710142651857.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)

5. 发布校园日记。用户可在“微录”模块，点击校园日记，可以查看学校内部所有人发布的动态（可进行打赏）同时可以点击右上角的图标，发布图片、或者是视频动态。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710144231440.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)
6. 旅行。用户可在“微游”模块，选择组团游、情侣游、或者单身游，选择其中某一个你感兴趣的旅行消息，然后定制自己的旅行计划，邀请成员、时间安排等。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710150409366.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)
7. 加好友。用户可通过“通讯录”模块，点击扫一扫，进入加好友流程。需要先写备注，名称等信息。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710144347897.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)
8. 我的：包含发布的动态、日记、二维码等
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710151352407.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)
### 三：App端核心技术
前端主要用到的核心技术：
- 融云即时通讯
    compile project (':IMKit')
- 2017年最新，抽屉式浮型侧滑菜单
    compile 'com.rom4ek:arcnavigationview:1.0.2'
-  网络请求框架retrofit2+rxjava
    compile 'io.reactivex:rxjava:1.2.0'
    compile 'io.reactivex:rxandroid:1.2.1'
    compile 'com.squareup.retrofit2:retrofit:2.0.2'
    compile 'com.squareup.retrofit2:converter-gson:2.0.2'
    compile 'com.squareup.retrofit2:adapter-rxjava:2.0.2'
- mob第三方手机验证
    compile name: 'SMSSDK-2.1.3', ext: 'aar'
    compile name: 'SMSSDKGUI-2.1.3', ext: 'aar'
- 省市县三级联动、和时间选择器
    compile 'com.airsaid.library:pickerview:1.0.3'
- zxing二维码扫描技术（参考github一品枫叶）
    compile 'cn.yipianfengye.android:zxing-library:2.1'
- cropview图片剪切技术
    compile 'com.oginotihiro:cropview:1.0.0'
- coordinatorTabLayOut效果
    compile 'cn.hugeterry.coordinatortablayout:coordinatortablayout:1.0.6'
-侧滑删除swipemenulistview
    compile 'com.baoyz.swipemenulistview:library:1.3.0'
- 多图选择器
    compile 'com.github.LuckSiege.PictureSelector:picture_library:v1.4.2'
- 百度地图POI地理编码和反地理编码
    compile files('libs/BaiduLBS_Android.jar')
- MaterialDesign的cardview效果
    compile 'com.android.support:design:25.3.1'
    compile 'com.android.support:appcompat-v7:25.3.1'
    compile 'com.android.support:cardview-v7:25.3.1'
- 新版recyclerView，带刷新加载
    compile 'com.wuxiaolong.pullloadmorerecyclerview:library:1.1.2'
- 图片显示九宫格ninegridview
    compile 'com.lzy.widget:ninegridview:0.2.0'
- 依附于listview、recyclerview的floatingactionbutton
    compile 'com.melnykov:floatingactionbutton:1.3.0'
- 张鸿洋的magic-viewpager和严正杰的viewPager定时器，完成3D轮播图的切换效果
    compile 'com.zhy:magic-viewpager:1.0.1'
- 七牛云文件上传架包,java端
    compile 'com.qiniu:qiniu-java-sdk:7.2.+'
- 七牛云文件存储，图片视频
    compile 'com.qiniu:qiniu-android-sdk:7.2.+'
- recyclerView加头部，recyclerviewheader2
    compile 'com.bartoszlipinski:recyclerviewheader2:2.0.1'
- 添加腾讯Bugly错误日志检测以及热修复
    compile 'com.tencent.bugly:crashreport:2.1.5'
    compile 'com.tencent.bugly:nativecrashreport:2.2.0'
- 引入litePal关系型sqLite数据库的封装
    compile 'org.litepal.android:core:1.6.0'
- 解决打包64k问题
    compile 'com.android.support:multidex:1.0.3'
- 引入leakCanary内存泄漏检测
    debugCompile 'com.squareup.leakcanary:leakcanary-android:1.5.4'
    releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.4'
- 引入smartRefreshLayout（下拉刷新上拉加载）
    compile 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.3'
    compile 'com.scwang.smartrefresh:SmartRefreshHeader:1.0.3'//没有使用特殊Header，可以不加这行
 ### 四：服务端整体架构
  采用SSH框架进行搭建SSH + Tomcat7 +Jdk1.7，主要功能就是对一些功能模块内容的增删改查（暂时还没有开发可视化的管理系统）。![在这里插入图片描述](https://img-blog.csdnimg.cn/20210710153523735.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTM3NjkyNzQ=,size_16,color_FFFFFF,t_70)
  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;时间过的真快，转眼间就五月份了，距离答辩日子越来越近了，我的毕业设计也接近了尾声，回忆从项目综合设计确定题目的那一刻，到现在完成了整个app项目，在这中间我收获了很多，一份耕耘，一份收获，看这自己完成的成果，此刻，内心无比充实，记得我是从去年7月份开始在苏州的一家互联网公司实习的，当初我对于Android的理解还不是那么深，因为自己也没有什么项目经验，也没作出成熟的app，所以来到公司，我不断地学习他们，每一次学习，每一次总结我都获益匪浅。而今我把自己的项目经验全部运用到了我的毕业设计中，包括一些当初没有实现的功能，现在也得到了解决，对于一些开源的框架问题也有了自己的理解。这次毕设真的让我收很多。
    
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在做毕设的时间里，我全身心地投入到软件开发中，开发过程中，遇到了各种各样的bug，有些是因为自己的疏忽大意没有考虑好，有些是一些新特性，我没有了解到，各种酸甜滋味，只有自己知道，我记得我曾经因为一个问题解决不了，晚上睡不着，第二天起来，发现了问题所在，早早地蹦了起来，把室友们都吓了一大跳。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过这次的毕业设计，巩固了我在大学里学到的很多知识，也让我的专业知识更加的牢固，之前以为有些很简单的东西，实际操作起来却是需要注意很多东西。项目当中我学到了很多需要注意的地方，同时，我也看到Android是一个日新月异的时代，新的东西在时时刻刻地充裕着我们眼帘，稍不留神，你可能就已经落后。在本次的毕业设计当中，我采用了很多新的框架，新的东西。有最新的网络请求框架retrofit+rxjava、侧滑浮型菜单、zxing二维码扫描、图片选择器、轮播图等。在调用框架的同时，更重要的是理解框架，我通过结合viewpager和定时器的理解，完成了3D切换轮播图的效果，很是兴奋，我相信这些成果在未来会是一笔财富。

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最后，附上项目的github地址：[https://github.com/zziafyc/GraduationProject](https://github.com/zziafyc/GraduationProject)，欢迎大家start和issue！