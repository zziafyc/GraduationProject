package com.zzia.graduationproject.ui.activity.me;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oginotihiro.cropview.CropView;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.api.ApiClient;
import com.zzia.graduationproject.api.MySubscriber;
import com.zzia.graduationproject.api.resp.BaseResp;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.base.Constants;
import com.zzia.graduationproject.event.StringEvent;
import com.zzia.graduationproject.model.User;
import com.zzia.graduationproject.qiniu.QiNiuFileUpload;
import com.zzia.graduationproject.utils.ImageUtils;
import com.zzia.graduationproject.utils.SharePreferenceUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class AvatarSettingActivity extends BaseActivity {
    public static final int REQUEST_PICK = 20;
    public static final int REQUEST_IMAGE = 30;
    @Bind(R.id.aas_avatar_iv)
    ImageView avatarIv;
    @Bind(R.id.acp_crop_cv)
    CropView mCropView;
    @Bind(R.id.aas_fromAlbum_lly)
    LinearLayout fromAlbumLy;
    @Bind(R.id.aas_takePhoto_lly)
    LinearLayout takePhotoLy;
    @Bind(R.id.title_right)
    TextView titleRightTv;
    Bitmap croppedBitmap;
    Uri imageUri;
    UserInfo mUserInfo;
    Configuration config = new Configuration.Builder()
            .connectTimeout(90)              // 链接超时。默认90秒
            .useHttps(true)                  // 是否使用https上传域名
            .useConcurrentResumeUpload(true) // 使用并发上传，使用并发上传时，除最后一块大小不定外，其余每个块大小固定为4M，
            .concurrentTaskCount(3)          // 并发上传线程数量为3
            .responseTimeout(90)             // 服务器响应超时。默认90秒
            .zone(FixedZone.zone2)           // 设置区域，不指定会自动选择。指定不同区域的上传域名、备用域名、备用IP。
            .build();
    private UploadManager mUploadManager = new UploadManager(config);


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_avatar_setting;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();
    }

    private void initViews() {
        setCustomTitle("设置个人头像");
        if (getIntent().getStringExtra("avatar") != null) {
            ImageUtils.setCornerImage(avatarIv, App.getUser().getAvatar());
        }
        mCropView.setVisibility(View.GONE);


    }

    private void initListener() {
        //从相册选择
        fromAlbumLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                startActivityForResult(intent, REQUEST_PICK);

            }
        });
        //拍一张招照片
        takePhotoLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 先拼接好一个路径：在内存卡/或是手机内存上做好文件夹
                String filePath = Environment.getExternalStorageDirectory().toString() + "/graduationProject";
                File localFile = new File(filePath);
                if (!localFile.exists()) {
                    localFile.mkdir();
                }
                File finalImageFile = new File(localFile, System.currentTimeMillis() + ".jpg");
                if (finalImageFile.exists()) {
                    finalImageFile.delete();
                }
                try {
                    finalImageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(finalImageFile);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
        //点击使用
        titleRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = ProgressDialog.show(AvatarSettingActivity.this, null, "Please wait…", true, false);
                mCropView.setVisibility(View.GONE);
                fromAlbumLy.setVisibility(View.VISIBLE);
                takePhotoLy.setVisibility(View.VISIBLE);
                titleRightTv.setVisibility(View.GONE);
                new Thread() {
                    public void run() {
                        croppedBitmap = mCropView.getOutput();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                avatarIv.setImageBitmap(croppedBitmap);
                            }
                        });

                       /* Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
                        CropUtil.saveOutput(AvatarSettingActivity.this, destination, croppedBitmap, 90);*/

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                        //然后将该图片上传至七牛云
                        String data = saveBitmap(croppedBitmap);
                        String key = "avatar_" + data.substring(data.lastIndexOf("/") + 1);
                        String token = QiNiuFileUpload.getToken();
                        mUploadManager.put(data, key, token, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                final String avatar = Constants.ThirdParty.QINIU_BUCKET + "/" + key;
                                User user = new User();
                                user.setUserId(App.getUser().getUserId());
                                user.setAvatar(avatar);
                                call(ApiClient.getApis().updateUserAvatar(user), new MySubscriber<BaseResp<Void>>() {
                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(BaseResp<Void> resp) {
                                        if (resp.resultCode == Constants.RespCode.SUCCESS) {
                                            if (resp.status == Constants.RespCode.SUCCESS) {
                                                User newUser = App.getUser();
                                                newUser.setAvatar(avatar);
                                                App.setUser(newUser);
                                                mUserInfo = new UserInfo(App.getUser().getUserId(), App.getUser().getNickName(), Uri.parse(App.getUser().getAvatar()));
                                                SharePreferenceUtils.put(AvatarSettingActivity.this, "user", newUser);
                                                EventBus.getDefault().post(new StringEvent("updateUserAvatar", avatar));
                                                //通知融云,然后刷新本地缓存
                                                RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                                                    @Override
                                                    public UserInfo getUserInfo(String s) {
                                                        return mUserInfo;
                                                    }
                                                }, true);
                                                RongIM.getInstance().refreshUserInfoCache(mUserInfo);
                                            }
                                        }

                                    }
                                });

                            }
                        }, new UploadOptions(null, "test-type", true, null, null));
                    }
                }.start();

            }
        });
    }

    //将bitmap保存到手机内存卡里
    public static String saveBitmap(Bitmap bitmap) {
        FileOutputStream out = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
        {    // 获取SDCard指定目录下
            String sdCardDir = Environment.getExternalStorageDirectory() + "/graduationProject";
            File dirFile = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            long currentTimeMillis = System.currentTimeMillis();
            File file = new File(sdCardDir, currentTimeMillis + ".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String path = Environment.getExternalStorageDirectory() + "/graduationProject/" + currentTimeMillis + ".jpg";
            return path;

        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK || requestCode == REQUEST_IMAGE) {
                fromAlbumLy.setVisibility(View.GONE);
                takePhotoLy.setVisibility(View.GONE);
                mCropView.setVisibility(View.VISIBLE);
                titleRightTv.setVisibility(View.VISIBLE);
                titleRightTv.setText("使用");
                Uri uri = null;
                if (requestCode == REQUEST_PICK) {
                    uri = data.getData();
                } else if (requestCode == REQUEST_IMAGE) {
                    uri = imageUri;
                }
                mCropView.of(uri).asSquare().initialize(AvatarSettingActivity.this);
            }

        }

    }
}
