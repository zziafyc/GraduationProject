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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>
 *     页面View操作工具类,eg:
 *     <ul>
 *         <li>当列表内容为空时，将ListView替换成EmptyView提示暂无内容</li>
 *         <li>当无网络时，将ContentView设置为ErrorView提示暂无网络</li>
 *         增强用户体验
 *     </ul>
 *
 * </p>
 *
 */
public class VaryViewHelper implements IVaryViewHelper {
	private View view; //目标操作视图targetView
	private ViewGroup parentView; //targetView 父视图
	private int viewIndex;//targetView 所在位置
	private ViewGroup.LayoutParams params;
	private View currentView; //页面当前显示视图

	/**
	 *
	 * @param view 目标操作视图
	 */
	public VaryViewHelper(View view) {
		super();
		this.view = view;
	}

	private void init() {
		params = view.getLayoutParams();
		if (view.getParent() != null) {
			parentView = (ViewGroup) view.getParent();
		} else {
			parentView = (ViewGroup) view.getRootView().findViewById(android.R.id.content);
		}
		int count = parentView.getChildCount();
		for (int index = 0; index < count; index++) {
			if (view == parentView.getChildAt(index)) {
				viewIndex = index;
				break;
			}
		}
		currentView = view;
	}

	/**
	 *
	 * @return 当前视图
	 */
	@Override
	public View getCurrentLayout() {
		return currentView;
	}

	/**
	 * 恢复原始视图
	 */
	@Override
	public void restoreView() {
		showLayout(view);
	}

	/**
	 * 设置视图
	 * @param view 需要显示的视图
	 */
	@Override
	public void showLayout(View view) {
		if (parentView == null) {
			init();
		}
		this.currentView = view;
		// 如果已经是那个view，那就不需要再进行替换操作了
		if (parentView.getChildAt(viewIndex) != view) {

			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}

			parentView.removeViewAt(viewIndex);
			parentView.addView(view, viewIndex, params);
		}
	}

	/**
	 * 加载视图
	 * @param layoutId
	 * @return
	 */
	@Override
	public View inflate(int layoutId) {
		return LayoutInflater.from(view.getContext()).inflate(layoutId, null);
	}

	/**
	 *
	 * @return 当前视图所在上下文
	 */
	@Override
	public Context getContext() {
		return view.getContext();
	}

	/**
	 *
	 * @return 原始视图
	 */
	@Override
	public View getView() {
		return view;
	}
}
