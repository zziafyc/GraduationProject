package com.zzia.graduationproject.ui.activity.register;

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

public class RegisterNameActivity extends BaseActivity {
    @Bind(R.id.arn_name_edit)
    EditText nameEdit;
    @Bind(R.id.arn_next_btn)
    TextView nextBtn;
    @Bind(R.id.arn_clear_name_lyt)
    RelativeLayout clearNameRv;
    private User mUser ;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register_name;
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
        initListener();

    }

    private void initListener() {
        //名称编辑
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringUtils.isEmpty(nameEdit.getText().toString())) {
                    nextBtn.setBackgroundResource(R.drawable.bg_button_white);
                    nextBtn.setClickable(false);
                    clearNameRv.setVisibility(View.GONE);
                } else {
                    nextBtn.setBackgroundResource(R.drawable.bg_button_blue);
                    nextBtn.setClickable(true);
                    clearNameRv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //清空按钮
        clearNameRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameEdit.setText("");
            }
        });
        //下一步
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(nameEdit.getText().toString())) {
                    return;
                }
                mUser.setNickName(nameEdit.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", mUser);
                go(RegisterSexActivity.class, bundle);
            }
        });
    }
}
