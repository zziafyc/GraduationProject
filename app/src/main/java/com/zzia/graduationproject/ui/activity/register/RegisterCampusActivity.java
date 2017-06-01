package com.zzia.graduationproject.ui.activity.register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;
import com.zzia.graduationproject.model.User;

import butterknife.Bind;

public class RegisterCampusActivity extends BaseActivity {
    @Bind(R.id.arn_name_edit)
    EditText campusNameEdt;
    @Bind(R.id.acc_clear_name_lyt)
    RelativeLayout clearNameRv;
    @Bind(R.id.acc_next_btn)
    TextView nextTv;
    private User mUser;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_choice_campus;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        if(extras.getSerializable("user")!=null){
            mUser= (User) extras.getSerializable("user");
        }
    }

    @Override
    protected void initViewsAndEvents() {
        initListener();

    }

    private void initListener() {
        campusNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                if (StringUtils.isEmpty(campusNameEdt.getText().toString())) {
                    nextTv.setBackgroundResource(R.drawable.bg_button_white);
                    nextTv.setClickable(false);
                    clearNameRv.setVisibility(View.GONE);
                } else {
                    nextTv.setBackgroundResource(R.drawable.bg_button_blue);
                    nextTv.setClickable(true);
                    clearNameRv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        clearNameRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                campusNameEdt.setText("");
            }
        });
        nextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(campusNameEdt.getText().toString())) {
                    return;
                }
                mUser.setCampusAddress(campusNameEdt.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", mUser);
                go(RegisterPasswordActivity.class, bundle);
            }
        });
    }
}
