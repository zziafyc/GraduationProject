package com.zzia.graduationproject.api;

import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.model.Diary;
import com.zzia.graduationproject.model.DiaryPraise;
import com.zzia.graduationproject.model.Friends;
import com.zzia.graduationproject.model.TravelPlan;
import com.zzia.graduationproject.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by fyc on 2017/3/6
 * 邮箱：847891359@qq.com
 * 博客：http://blog.csdn.net/u013769274
 */

public interface Apis {
    //关键字搜索用户
    @POST("userAction_getUserInfo")
    Observable<BaseResp<User>> getUserInfo(@Query("userId") String userId);

    //用户注册
    @POST("userAction_register")
    Observable<BaseResp<User>> register(@Body User user);

    //用户登录
    @POST("userAction_login")
    Observable<BaseResp<User>> login(@Body User user);

    //得到所有好友
    @POST("userAction_getAllFriends")
    Observable<BaseResp<Map<String,List<User>>>> getAllFriends(@Query("userId") String userId);

   //添加好友
    @POST("userAction_addFriend")
    Observable<BaseResp<Void>> addFriend(@Body Friends friends);

    //修改登录状态
    @POST("userAction_updateLoginState")
    Observable<BaseResp<Void>> updateLoginState(@Query("userId") String userId);

    //更新用户图像
    @POST("userAction_updateUserAvatar")
    Observable<BaseResp<Void>> updateUserAvatar(@Body User user);

    //日记点赞和取消点赞接口
    @POST("diaryAction_setPraiseState")
    Observable<BaseResp<Void>> setPraiseState(@Body DiaryPraise diaryPraise);

    //删除动态接口
    @POST("diaryAction_deleteDiary")
    Observable<BaseResp<Void>> deleteDiary(@Query("diaryId") String diaryId);

    //得到校园所有动态接口
    @POST("diaryAction_getAllDiary")
    Observable<BaseResp<List<Diary>>> getAllDiary(@Query("userId") String userId,@Query("currentPage") int currentPage,@Query("count") int count);

    //得到好友的所有动态接口
    @POST("diaryAction_getAllFriendsDiary")
    Observable<BaseResp<List<Diary>>> getAllFriendsDiary(@Query("userId") String userId,@Query("currentPage") int currentPage,@Query("count") int count);

    //得到我发布的所有动态接口
    @POST("diaryAction_getAllMyDiary")
    Observable<BaseResp<List<Diary>>> getAllMyDiary(@Query("userId") String userId,@Query("currentPage") int currentPage,@Query("count") int count);

    //添加日记接口
    @POST("diaryAction_addDiary")
    Observable<BaseResp<Void>> addDiary(@Body Diary diary);

    //根据字段模糊搜索用户加好友,key是好友的手机号或者是昵称
    @POST("userAction_searchUser")
    Observable<BaseResp<List<User>>> searchUser(@Query("key") String key);

    //获取所有好友list
    @POST("userAction_getAllFriendsList")
    Observable<BaseResp<List<User>>> getAllFriendsList(@Query("userId") String userId);

    //删除好友申请记录
    @POST("userAction_deleteFriendMessage")
    Observable<BaseResp<Void>> deleteFriendMessage(@Query("id") String id);

    //得到所有加我为好友的用户
    @POST("userAction_getAllFriendMessage")
    Observable<BaseResp<List<Friends>>> getAllFriendMessage(@Query("userId") String userId,@Query("currentPage") int currentPage,@Query("count") int count);

    //同意，改变消息状态
    @POST("userAction_changeMessageState")
    Observable<BaseResp<Void>> changeMessageState(@Query("id") String id,@Query("remark") String remark);

    //修改备注
    @POST("userAction_changeRemark")
    Observable<BaseResp<Void>> changeRemark(@Query("id") String id,@Query("remark") String remark);

    //得到我的莫一种旅行的列表接口
    @POST("travelAction_getTravelListByType")
    Observable<BaseResp<List<TravelPlan>>> getTravelListByType(@Query("createId") String createId, @Query("type") int type, @Query("currentPage") int currentPage, @Query("count") int count);

    //创建一个travelPlan
    @POST("travelAction_createTravelPlan")
    Observable<BaseResp<String>> createTravelPlan(@Body TravelPlan travelPlan);

    //得到某一条travel的想起请
    @POST("travelAction_getTravelPlanDetail")
    Observable<BaseResp<TravelPlan>> getTravelPlanDetail(@Query("travelId") String travelId);

}
