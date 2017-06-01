package com.zzia.graduationproject.ui.activity.tellbook;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.utils.QrImageUtils;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/2/28.
 * 邮箱：wgyscsf@163.com
 * 博客：http://blog.csdn.net/wgyscsf
 */

public class CustomCaptureActivity extends BaseActivity {
    public static final int REQUEST_IMG = 2;
    @Bind(R.id.ac_btn_cancel)
    Button cancelBtn;
    @Bind(R.id.ac_btn_album)
    Button albumBtn;
    @Bind(R.id.title_iv_right)
    ImageView openFlashTv;
    boolean isOpen;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_customcapture;
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initListener();

    }


    private void initViews() {
        openFlashTv.setVisibility(View.VISIBLE);
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描框设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        // 二维码解析回调函数
        CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                Bundle bundle = new Bundle();
                bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                bundle.putString(CodeUtils.RESULT_STRING, result);
                goAndFinish(AddFriendActivity.class, bundle);
            }

            @Override
            public void onAnalyzeFailed() {
                Bundle bundle = new Bundle();
                bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
                bundle.putString(CodeUtils.RESULT_STRING, "");
                showToast("扫描失败，请重试！");
            }
        };
        captureFragment.setAnalyzeCallback(analyzeCallback);
        //替换我们的扫描控件
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

    }

    private void initListener() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMG);
            }
        });
        openFlashTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Camera camera= Camera.open();
                //关闭闪光灯
                if(isOpen){
                    if (camera != null) {
                        Camera.Parameters parameter = camera.getParameters();
                        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameter);
                        camera.startPreview();
                    }
                    isOpen=false;
                    openFlashTv.setBackgroundResource(R.drawable.flash_close);

                }else{
                    // 打开闪光灯
                    if (camera != null) {
                        Camera.Parameters parameter = camera.getParameters();
                        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameter);
                        camera.stopPreview();
                    }
                    isOpen=true;
                    openFlashTv.setBackgroundResource(R.drawable.flash_open);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMG) {
            if (null != data) {
                Uri uri = data.getData();
                ContentResolver cr = getContentResolver();
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片
                    CodeUtils.analyzeBitmap(QrImageUtils.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Toast.makeText(CustomCaptureActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                            Bundle bundle = new Bundle();
                            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                            bundle.putString(CodeUtils.RESULT_STRING, result);
                            goAndFinish(AddFriendActivity.class, bundle);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            showToast("二维码解析失败，请重试！");
                        }
                    });
                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
