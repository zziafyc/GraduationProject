package com.zzia.graduationproject.ui.activity.register;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airsaid.pickerviewlibrary.CityPickerView;
import com.airsaid.pickerviewlibrary.listener.OnSimpleCitySelectListener;
import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.model.User;

import java.io.Serializable;

import butterknife.Bind;

public class RegisterCityActivity extends BaseActivity {
    @Bind(R.id.arc_city_tv)
    TextView cityTv;
    @Bind(R.id.arn_next_btn)
    TextView nextBtn;
    private User mUser;
    private CityPickerView mCityPickerView;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register_city;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if (extras.getSerializable("user") != null) {
            mUser = (User) extras.getSerializable("user");
        }

    }

    @Override
    protected void initViewsAndEvents() {
        initPickupView();
        initListener();

    }

    private void initPickupView() {
        mCityPickerView = new CityPickerView(this);
        // 设置点击外部是否消失
        mCityPickerView.setCancelable(true);
        //设置滚轮字体大小
        mCityPickerView.setTextSize(18f);
        //设置标题
        mCityPickerView.setTitle(getString(R.string.choiceLocation2));
        // 设置取消文字
        mCityPickerView.setCancelText(getString(R.string.cancel));
        // 设置取消文字颜色
        mCityPickerView.setCancelTextColor(getResources().getColor(R.color.black));
        // 设置取消文字大小
        mCityPickerView.setCancelTextSize(14f);
        // 设置确定文字
        mCityPickerView.setSubmitText(getString(R.string.sure));
        // 设置确定文字颜色
        mCityPickerView.setSubmitTextColor(getResources().getColor(R.color.black));
        // 设置确定文字大小
        mCityPickerView.setSubmitTextSize(14f);
        // 设置头部背景
        mCityPickerView.setHeadBackgroundColor(getResources().getColor(R.color.gray_bg));
        mCityPickerView.setOnCitySelectListener(new OnSimpleCitySelectListener() {
            @Override
            public void onCitySelect(String prov, String city, String area) {
                // 省、市、区 分开获取
                cityTv.setText(prov + " " + city + " " + area);
                mUser.setAddress(prov + " " + city + " " + area);
            }

            @Override
            public void onCitySelect(String str) {
                // 一起获取
                //showToast("选择了：" + str);
            }
        });

    }

    private void initListener() {
        cityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCityPickerView.show();
            }
        });
        cityTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.isEmpty(cityTv.getText().toString())) {
                    nextBtn.setBackgroundResource(R.drawable.bg_button_white);
                    nextBtn.setClickable(false);
                } else {
                    nextBtn.setBackgroundResource(R.drawable.bg_button_blue);
                    nextBtn.setClickable(true);
                }

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(cityTv.getText().toString())) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", mUser);
                go(RegisterCampusActivity.class, bundle);
            }
        });
    }
}
