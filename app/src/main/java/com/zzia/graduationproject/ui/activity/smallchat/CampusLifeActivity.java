package com.zzia.graduationproject.ui.activity.smallchat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.luck.picture.lib.ui.PictureVideoPlayActivity;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.oginotihiro.cropview.CropUtil;
import com.shcyd.lib.utils.StringUtils;
import com.yalantis.ucrop.entity.LocalMedia;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.base.recyclerview.CommonAdapter;
import com.zzia.graduationproject.base.recyclerview.MyDividerItemDecoration;
import com.zzia.graduationproject.base.recyclerview.ViewHolder;
import com.zzia.graduationproject.model.Diary;
import com.zzia.graduationproject.model.DiaryPraise;
import com.zzia.graduationproject.model.PhotoConnect;
import com.zzia.graduationproject.model.VideoConnect;
import com.zzia.graduationproject.ui.activity.me.AvatarSettingActivity;
import com.zzia.graduationproject.utils.ImgThumbUtils;
import com.zzia.graduationproject.utils.WindowUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static android.provider.MediaStore.Video.Thumbnails.MICRO_KIND;
import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;

public class CampusLifeActivity extends BaseActivity {
    @Bind(R.id.title_iv_rightImgView)
    ImageView rightImgView;
    @Bind(R.id.acl_campus_lv)
    RecyclerView mRecyclerView;
    CommonAdapter<Diary> mAdapter;
    List<Diary> mList = new ArrayList<>();
    Dialog deleteDiaryDialog;
    PopupWindow mPop;
    List<LocalMedia> selectMedia = new ArrayList<>();
    private int selectMode = FunctionConfig.MODE_MULTIPLE; //多选模式
    private int maxSelectNum = 9;// 图片最大可选数量
    private int selectType = FunctionConfig.TYPE_IMAGE;
    private boolean enablePreview = true;
    private boolean isPreviewVideo = true;
    private boolean isShow = true;
    private boolean enableCrop = false;
    private boolean theme = false;
    private boolean selectImageType = false;
    private boolean isCheckNumMode = false;
    private int themeStyle;
    private int previewColor;
    private int completeColor;
    private int checkedBoxDrawable;  //图片选择的checkbox风格
    private int previewBottomBgColor;  //预览底部颜色
    private int bottomBgColor;
    String campusLife;
    String friendsTrend;
    String mySendDiary;
    int currentPage=0;
    int count =10;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_campus_life;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        campusLife = extras.getString("campusLife");
        friendsTrend = extras.getString("friendsTrend");
        mySendDiary = extras.getString("mySendDiary");

    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initAdapter();
        initData();
        initPop();
        initListener();

    }

    private void initViews() {
        rightImgView.setVisibility(View.VISIBLE);
    }

    private void initAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(CampusLifeActivity.this, DividerItemDecoration.VERTICAL));
        mAdapter = new CommonAdapter<Diary>(this, R.layout.item_trend, mList) {
            @Override
            protected void convert(final ViewHolder holder, final Diary diary, int position) {
                holder.setNetImage(R.id.idn_btn_pic, diary.getUser().getAvatar());
                holder.setText(R.id.idn_tv_name, diary.getUser().getNickName());
                holder.setText(R.id.idn_tv_description, diary.getDiaryContent());
                holder.setText(R.id.idn_tv_location, diary.getSendAddress());
                holder.setText(R.id.idn_tv_sendTime,diary.getCreateDateTransfer());
                //全文点击效果
                holder.setOnClickListener(R.id.idn_tv_allContentLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                //图片九宫格
                NineGridView nineGridView = holder.getView(R.id.idn_ngv_nineGridView);
                List<PhotoConnect> photos = diary.getPhotoList();
                List<ImageInfo> images = new ArrayList<>();
                if (photos != null && photos.size() > 0) {
                    nineGridView.setVisibility(View.VISIBLE);
                    for (PhotoConnect photo : photos) {
                        ImageInfo info = new ImageInfo();
                        info.setThumbnailUrl(photo.getPhotoSite());
                        info.setBigImageUrl(photo.getPhotoSite());
                        images.add(info);
                    }
                    nineGridView.setAdapter(new NineGridViewClickAdapter(CampusLifeActivity.this, images));
                } else {
                    nineGridView.setVisibility(View.GONE);
                }
                //视频
                final VideoConnect video = diary.getVideoConnect();
                if (video != null) {
                    holder.setVisible(R.id.idn_ll_video, true);
                    new Thread() {
                        public void run() {
                            final Bitmap bitmap = ImgThumbUtils.createVideoThumbnail(video.getVideoSite(), MINI_KIND);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.setImageBitmap(R.id.isiud_video_iv, bitmap);
                                }
                            });

                        }
                    }.start();

                    holder.setOnClickListener(R.id.isiud_video_logoIv, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //进入播放视频页面
                            Bundle bundle = new Bundle();
                            bundle.putString("video_path", video.getVideoSite());
                            go(PictureVideoPlayActivity.class, bundle);
                        }
                    });
                } else {
                    holder.setVisible(R.id.idn_ll_video, false);
                }
                //点赞
                final TextView praiseTv = holder.getView(R.id.idn_tv_praise);
                final TextView praiseCount = holder.getView(R.id.idn_tv_praiseNum);
                if (diary.isHavePraise()) {
                    praiseTv.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorYellow));
                    praiseCount.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorYellow));
                } else {
                    praiseTv.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorBlowLabel));
                    praiseCount.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorBlowLabel));
                }
                if (diary.getPraiseCount() == 0) {
                    praiseCount.setText(" ");
                } else {
                    praiseCount.setText(String.valueOf(diary.getPraiseCount()));
                }
                //点赞和取消点赞，点击事件
                holder.setOnClickListener(R.id.idn_ll_praise, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (diary.isHavePraise()) {
                            diary.setHavePraise(false);
                            if (praiseCount.getText().toString().equals("1")) {
                                praiseCount.setText(" ");
                            } else {
                                praiseCount.setText((Integer.parseInt(praiseCount.getText().toString()) - 1 + ""));
                            }
                            praiseTv.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorBlowLabel));
                            praiseCount.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorBlowLabel));
                            diary.setPraiseCount(diary.getPraiseCount() - 1);
                        } else {
                            diary.setHavePraise(true);
                            if (praiseCount.getText().toString().equals(" ")) {
                                praiseCount.setText("1");
                            } else {
                                praiseCount.setText((Integer.parseInt(praiseCount.getText().toString()) + 1 + ""));
                            }
                            praiseTv.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorYellow));
                            praiseCount.setTextColor(ContextCompat.getColor(CampusLifeActivity.this, R.color.colorYellow));
                            diary.setPraiseCount(diary.getPraiseCount() + 1);
                        }
                        //点赞开始请求接口
                        DiaryPraise praise = new DiaryPraise();
                        praise.setUserId(diary.getUserId());
                        praise.setDiaryId(diary.getDiaryId());
                        call(ApiClient.getApis().setPraiseState(praise), new MySubscriber<BaseResp<Void>>() {
                            @Override
                            public void onError(Throwable e) {
                                //数据需要回滚
                                if (diary.isHavePraise()) {
                                    //以为点赞了，实际没有成功
                                    diary.setHavePraise(false);
                                    diary.setPraiseCount(diary.getPraiseCount() - 1);
                                } else {
                                    //以为取消点赞了，实际没有成功
                                    diary.setHavePraise(true);
                                    diary.setPraiseCount(diary.getPraiseCount() + 1);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onNext(BaseResp<Void> resp) {

                            }

                        });

                    }
                });
                //评论
                if (diary.getCommentCount() == 0) {
                    holder.setText(R.id.idn_tv_commemtNum, " ");
                } else {
                    holder.setText(R.id.idn_tv_commemtNum, String.valueOf(diary.getCommentCount()));
                }
                holder.setOnClickListener(R.id.idn_tv_commemtLayout, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //进入评论详情页
                    }
                });

                //删除该条动态，如果是本人发的动态可以删除，如果是别人发的动态则删除按钮隐藏
                if (diary.getUserId().equals(App.getUser().getUserId())) {
                    holder.setVisible(R.id.idn_tv_delete, true);
                } else {
                    holder.setVisible(R.id.idn_tv_delete, false);
                }
                holder.setOnClickListener(R.id.idn_tv_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDiaryDialog = new Dialog(CampusLifeActivity.this, R.style.NoTitleDialogStyle);
                        deleteDiaryDialog.setContentView(R.layout.dialog_diary_delete);
                        TextView mTextView_title = (TextView) deleteDiaryDialog.findViewById(R.id.ddd_des_txt);
                        TextView mTextView_sure = (TextView) deleteDiaryDialog.findViewById(R.id.ddd_tv_sure);
                        TextView mTextView_cancel = (TextView) deleteDiaryDialog.findViewById(R.id.ddd_tv_cancel);
                        mTextView_title.setText("确定要删除吗？");
                        mTextView_sure.setOnClickListener(this);
                        deleteDiaryDialog.setCanceledOnTouchOutside(true);
                        deleteDiaryDialog.show();
                        mTextView_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDiary(diary);
                            }
                        });
                        mTextView_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDiaryDialog.dismiss();
                            }
                        });
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        if (!StringUtils.isEmpty(campusLife)) {
            call(ApiClient.getApis().getAllDiary(App.getUser().getUserId(),currentPage,count), new MySubscriber<BaseResp<List<Diary>>>() {
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(BaseResp<List<Diary>> resp) {
                    if (resp.resultCode == Constants.RespCode.SUCCESS) {
                        if (resp.status == Constants.RespCode.SUCCESS) {
                            currentPage++;
                            mList.addAll(resp.data);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

            });
        } else if (!StringUtils.isEmpty(friendsTrend)) {
            call(ApiClient.getApis().getAllFriendsDiary(App.getUser().getUserId(),currentPage,count), new MySubscriber<BaseResp<List<Diary>>>() {
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(BaseResp<List<Diary>> resp) {
                    if (resp.resultCode == Constants.RespCode.SUCCESS) {
                        if (resp.status == Constants.RespCode.SUCCESS) {
                            currentPage++;
                            mList.addAll(resp.data);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

            });
        }else if(!StringUtils.isEmpty(mySendDiary)){
            call(ApiClient.getApis().getAllMyDiary(App.getUser().getUserId(),currentPage,count), new MySubscriber<BaseResp<List<Diary>>>() {
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(BaseResp<List<Diary>> resp) {
                    if (resp.resultCode == Constants.RespCode.SUCCESS) {
                        if (resp.status == Constants.RespCode.SUCCESS) {
                            currentPage++;
                            mList.addAll(resp.data);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

            });
        }
    }

    private void initPop() {
        View view = getLayoutInflater().inflate(R.layout.pop_send_trend, null);
        TextView sendPic = (TextView) view.findViewById(R.id.pdp_image_txt);
        TextView sendVideo = (TextView) view.findViewById(R.id.pdp_video_txt);
        TextView cancel = (TextView) view.findViewById(R.id.pdp_cancel_txt);
        mPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPop.setTouchable(true);
        mPop.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowUtils.backgroundAlpha(CampusLifeActivity.this, 1f);
            }
        });
        sendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPop.dismiss();
                selectType = FunctionConfig.TYPE_IMAGE;
                initOption();
            }
        });
        sendVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPop.dismiss();
                selectType = FunctionConfig.TYPE_VIDEO;
                initOption();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPop.dismiss();
            }
        });

    }

    private void deleteDiary(final Diary diary) {
        call(ApiClient.getApis().deleteDiary(diary.getDiaryId()), new MySubscriber<BaseResp<Void>>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResp<Void> resp) {
                mList.remove(diary);
                mAdapter.notifyDataSetChanged();
                deleteDiaryDialog.dismiss();

            }
        });


    }

    private void initOption() {
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(selectType) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setCompress(false) //是否压缩
                .setEnablePixelCompress(true) //是否启用像素压缩
                .setEnableQualityCompress(true) //是否启质量压缩
                .setMaxSelectNum(maxSelectNum) // 可选择图片的数量
                .setSelectMode(selectMode) // 单选 or 多选
                .setShowCamera(isShow) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                .setEnablePreview(enablePreview) // 是否打开预览选项
                .setEnableCrop(enableCrop) // 是否打开剪切选项
                .setPreviewVideo(isPreviewVideo) // 是否预览视频(播放) mode or 多选有效
                .setCheckedBoxDrawable(checkedBoxDrawable)
                .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
                .setRecordVideoSecond(60) // 视频秒数
                .setGif(false)// 是否显示gif图片，默认不显示
                .setPreviewColor(previewColor) //预览字体颜色
                .setCompleteColor(completeColor) //已完成字体颜色
                .setPreviewBottomBgColor(previewBottomBgColor) //预览底部背景色
                .setBottomBgColor(bottomBgColor) //图片列表底部背景色
                .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
                .setCheckNumMode(isCheckNumMode)
                .setCompressQuality(100) // 图片裁剪质量,默认无损
                .setImageSpanCount(3) // 每行个数
                .setSelectMedia(selectMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
                .setThemeStyle(themeStyle) // 设置主题样式
                .create();
        // 先初始化参数配置，在启动相册
        PictureConfig.getInstance().init(options).openPhoto(CampusLifeActivity.this, resultCallback);

    }

    private void initListener() {
        rightImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPop.showAtLocation(findViewById(R.id.activity_campus_life), Gravity.BOTTOM, 0, 0);
                WindowUtils.backgroundAlpha(CampusLifeActivity.this, 0.5f);
            }
        });
    }

    /**
     * 图片视频回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
            Log.i("callBack_result", selectMedia.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                String path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                String path = media.getCompressPath();
            } else {
                // 原图地址
                String path = media.getPath();
            }
            if (selectMedia != null) {

                Bundle bundle = new Bundle();
                bundle.putInt("selectType", selectType);
                bundle.putSerializable("selectMedia", (Serializable) selectMedia);
                goAndFinish(SendTrendActivity.class, bundle);
            }
        }
    };
}
