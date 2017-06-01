/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shcyd.lib.helper;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.shcyd.lib.R;
import com.shcyd.lib.utils.StringUtils;

import butterknife.ButterKnife;

/**
 * 常用视图替换操作的帮助类，如：网络错误、异常提示、空内容提示、加载中
 */
public class VaryViewHelperController {

    private IVaryViewHelper helper;

    public VaryViewHelperController(View view) {
        this(new VaryViewHelper(view));
    }

    public VaryViewHelperController(IVaryViewHelper helper) {
        super();
        this.helper = helper;
    }

    /**
     * 网络错误提示
     *
     * @param onClickListener
     */
    public void showNetworkError(View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.message);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
//        textView.setText(helper.getContext().getResources().getString(R.string.common_no_network_msg));

        textView.setVisibility(View.GONE);
        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.tips_no_network);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    /**
     * 异常错误提示
     *
     * @param errorMsg
     * @param onClickListener
     */
    public void showError(String errorMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.message);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!StringUtils.isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_error_msg));
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.ic_exception);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        helper.showLayout(layout);
    }

    /**
     * 无内容提示
     *
     * @param emptyMsg
     * @param onClickListener
     */
    public void showEmpty(String emptyMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.common_none);
        TextView textView = (TextView) layout.findViewById(R.id.empty_msg);
        if (!StringUtils.isEmpty(emptyMsg)) {
            textView.setText(emptyMsg);
        }
//        else {
//            textView.setText(helper.getContext().getResources().getString(R.string.common_empty_msg));
//        }
//
//        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
//        imageView.setImageResource(R.drawable.ic_exception);
//
//        if (null != onClickListener) {
//            layout.setOnClickListener(onClickListener);
//        }

        helper.showLayout(layout);
    }

    public void showCustomEmpty(int resId, String msg, View.OnClickListener listener) {
        View layout = helper.inflate(R.layout.common_none);
        if (resId > 0) {
            ImageView emptyImg = ButterKnife.findById(layout, R.id.empty_img);
            emptyImg.setImageResource(resId);

            if (listener != null)
                emptyImg.setOnClickListener(listener);
        }

        if (!StringUtils.isEmpty(msg)) {
            TextView tv_empty_msg = ButterKnife.findById(layout, R.id.empty_msg);
            tv_empty_msg.setText(msg);
        }
        helper.showLayout(layout);
    }

    /**
     * 加载中
     *
     * @param msg
     */
    public void showLoading(String msg) {
        View layout = helper.inflate(R.layout.loading);
//        if (!StringUtils.isEmpty(msg)) {
//            TextView textView = (TextView) layout.findViewById(R.id.loading_msg);
//            textView.setText(msg);
//        }
        helper.showLayout(layout);
//        Toast.makeText(helper.getContext(), "loading...", Toast.LENGTH_SHORT).show();
    }

    /**
     * 还原
     */
    public void restore() {
        helper.restoreView();
    }
}
