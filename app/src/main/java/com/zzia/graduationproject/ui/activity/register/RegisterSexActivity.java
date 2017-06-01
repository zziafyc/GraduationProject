package com.zzia.graduationproject.ui.activity.register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.model.User;

import butterknife.Bind;

public class RegisterSexActivity extends BaseActivity {
    @Bind(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @Bind(R.id.radioBtn1)
    RadioButton radioBtn1;
    @Bind(R.id.radioBtn2)
    RadioButton radioBtn2;
    @Bind(R.id.arn_next_btn)
    TextView nextBtn;
    String sex="男生";
    private User mUser ;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register_sex;
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
        initViews();
        initListener();

    }

    private void initViews() {
        nextBtn.setBackgroundResource(R.drawable.bg_button_blue);

    }

    private void initListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioBtn1:
                        sex="男生";
                        break;
                    case R.id.radioBtn2:
                        sex="女生";
                        break;
                }
            }
        });
        //下一步
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.setSex(sex);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", mUser);
                go(RegisterCityActivity.class, bundle);
            }
        });
    }
}
