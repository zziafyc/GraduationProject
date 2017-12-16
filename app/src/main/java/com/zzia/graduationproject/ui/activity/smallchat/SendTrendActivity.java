package com.zzia.graduationproject.ui.activity.smallchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.shcyd.lib.utils.StringUtils;
import com.umeng.socialize.utils.SocializeUtils;
import com.yalantis.ucrop.entity.LocalMedia;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.adapter.GridImageAdapter;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.event.TrendEvent;
import com.zzia.graduationproject.model.Diary;
import com.zzia.graduationproject.model.PhotoConnect;
import com.zzia.graduationproject.model.VideoConnect;
import com.zzia.graduationproject.qiniu.QiNiuFileUpload;
import com.zzia.graduationproject.utils.FullyGridLayoutManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.eventbus.EventBus;

public class SendTrendActivity extends BaseActivity {
    @Bind(R.id.as_pics_rv)
    RecyclerView picsRecyclerView;
    @Bind(R.id.ast_location_lyt)
    RelativeLayout locationLay;
    @Bind(R.id.ast_location_img)
    ImageView locationImg;
    @Bind(R.id.ast_location_txt)
    TextView selectLocation;
    @Bind(R.id.ast_cancel_tv)
    TextView cancelTv;
    @Bind(R.id.ast_send_tv)
    TextView sendTv;
    @Bind(R.id.ast_content_edit)
    EditText contentEdt;
    private ProgressDialog mProgressDialog;

    GridImageAdapter mAdapter;
    public static final int REQUEST_LOCATION = 18;
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
    private List<LocalMedia> selectMedia = new ArrayList<>();  //已选的
    private UploadManager mUploadManager = new UploadManager();
    int k = 0;//标记已上传的图片数
    private List<PhotoConnect> mPhotoList = new ArrayList<>();
    private List<VideoConnect> mVideoConnectList = new ArrayList<>();
    private String sendAddress;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_send_trend;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        selectMedia = (List<LocalMedia>) extras.getSerializable("selectMedia");
        selectType = extras.getInt("selectType");
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initAdapter();
        initListener();

    }

    private void initViews() {
        mProgressDialog = new ProgressDialog(this);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(SendTrendActivity.this, 4, GridLayoutManager.VERTICAL, false);
        picsRecyclerView.setLayoutManager(manager);
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }

    }

    private void initAdapter() {
        mAdapter = new GridImageAdapter(SendTrendActivity.this, onAddPicClickListener);
        mAdapter.setList(selectMedia);
        mAdapter.setSelectMax(9);
        picsRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectType) {
                    case FunctionConfig.TYPE_IMAGE:
                        // 预览图片
                        PictureConfig.getInstance().externalPicturePreview(SendTrendActivity.this, position, selectMedia);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // 预览视频
                        if (selectMedia.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(SendTrendActivity.this, selectMedia.get(position).getPath());
                        }
                        break;
                }

            }
        });
    }

    private void initListener() {
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        locationLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendTrendActivity.this, LocationActivity.class);
                Bundle bundle = new Bundle();
                if (!StringUtils.isEmpty(selectLocation.getText().toString())) {
                    bundle.putString("selectLocation", selectLocation.getText().toString());
                }
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });
        sendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < selectMedia.size(); i++) {
                    sendDiary(selectMedia.get(i).getPath());
                }

            }
        });
    }

    private void sendDiary(String filePath) {
        String token = QiNiuFileUpload.getToken();
        String data = filePath; //文件实际路径
        String key = "";  //文件名
        if (selectType == FunctionConfig.TYPE_IMAGE) {
            key = "image_" + System.currentTimeMillis() + "." + data.split("\\.")[1];
            upLoadPic(data, key, token);
        } else if (selectType == FunctionConfig.TYPE_VIDEO) {
            key = "video_" + System.currentTimeMillis() + "." + data.split("\\.")[1];
            upLoadVideo(data, key, token);
        }
        SocializeUtils.safeShowDialog(mProgressDialog);
    }

    private void upLoadPic(String data, String key, String token) {
        mUploadManager.put(data, key, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo info, JSONObject response) {
                PhotoConnect photoConnect = new PhotoConnect();
                photoConnect.setPhotoSite(Constants.ThirdParty.QINIU_BUCKET + "/" + key);
                mPhotoList.add(photoConnect);
                k++;
                if (k == selectMedia.size()) {
                    //把地址写到服务端
                    Diary diary = new Diary();
                    diary.setPhotoList(mPhotoList);
                    diary.setDiaryContent(contentEdt.getText().toString());
                    diary.setUserId(App.getUser().getUserId());
                    diary.setSendAddress(sendAddress);
                    call(ApiClient.getApis().addDiary(diary), new MySubscriber<BaseResp<Void>>() {
                        @Override
                        public void onError(Throwable e) {
                            SocializeUtils.safeCloseDialog(mProgressDialog);
                        }

                        @Override
                        public void onNext(BaseResp<Void> resp) {
                            if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                if (resp.status == Constants.RespCode.SUCCESS) {
                                    EventBus.getDefault().post(new TrendEvent("updateTrend"));
                                    SocializeUtils.safeCloseDialog(mProgressDialog);
                                    finish();
                                }
                            }
                        }

                    });

                }
            }
        }, new UploadOptions(null, "test-type", true, null, null));

    }

    private void upLoadVideo(String data, String key, String token) {
        mUploadManager.put(data, key, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo info, JSONObject response) {
                VideoConnect videoConnect = new VideoConnect();
                videoConnect.setVideoSite(Constants.ThirdParty.QINIU_BUCKET + "/" + key);
                mVideoConnectList.add(videoConnect);
                k++;
                if (k == selectMedia.size()) {
                    //把地址写到服务端
                    Diary diary = new Diary();
                    diary.setVideoConnect(mVideoConnectList.get(0));
                    diary.setDiaryContent(contentEdt.getText().toString());
                    diary.setUserId(App.getUser().getUserId());
                    diary.setSendAddress(sendAddress);
                    call(ApiClient.getApis().addDiary(diary), new MySubscriber<BaseResp<Void>>() {
                        @Override
                        public void onError(Throwable e) {
                            SocializeUtils.safeCloseDialog(mProgressDialog);
                        }

                        @Override
                        public void onNext(BaseResp<Void> resp) {
                            if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                if (resp.status == Constants.RespCode.SUCCESS) {
                                    SocializeUtils.safeCloseDialog(mProgressDialog);
                                    EventBus.getDefault().post(new TrendEvent("updateTrend"));
                                    finish();
                                }
                            }
                        }

                    });

                }
            }
        }, new UploadOptions(null, "test-type", true, null, null));

    }

    /**
     * 添加删除图片回调接口
     */

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    if (theme) {
                        // 设置主题样式
                        themeStyle = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                    } else {
                        themeStyle = ContextCompat.getColor(getApplicationContext(), R.color.bar_grey);
                    }
                    if (isCheckNumMode) {
                        // QQ 风格模式下 这里自己搭配颜色
                        previewColor = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                        completeColor = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                    } else {
                        previewColor = ContextCompat.getColor(getApplicationContext(), R.color.tab_color_true);
                        completeColor = ContextCompat.getColor(getApplicationContext(), R.color.tab_color_true);
                    }
                    if (selectImageType) {
                        checkedBoxDrawable = R.drawable.select_cb;
                    } else {
                        checkedBoxDrawable = 0;
                    }

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
                    PictureConfig.getInstance().init(options).openPhoto(SendTrendActivity.this, resultCallback);

                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    /**
     * 图片回调方法
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
                mAdapter.setList(selectMedia);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            if (data == null) {
                return;
            }
            String location = data.getExtras().getString("selectLocation");
            if (location != null) {
                selectLocation.setText(location);
                locationImg.setBackgroundResource(R.drawable.fasong_dingwei);
                sendAddress = location;
            } else {
                selectLocation.setText("所在位置");
                locationImg.setBackgroundResource(R.drawable.fabiao_weizhi);
                sendAddress = "";
            }

        }
    }
}
