/*
 * Copyright 2018. who<980008027@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsdroid.codeview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/2/11.
 */

public class CodeEditor extends HVScrollView {
	CodeText mCodeText;
	int mCodeTextMinHeight;
	int mCodeTextMinWidth;

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public CodeEditor(Context context) {
		super(context);
		init();
	}

	public CodeEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setBackgroundColor(0XFF333333);
		mCodeText = new CodeText(getContext());
		mCodeText.scrollView = this;
		addView(mCodeText);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		int codeWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		int codeHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		if (mCodeTextMinHeight != codeHeight || mCodeTextMinWidth != codeWidth) {
			mCodeTextMinWidth = codeWidth;
			mCodeTextMinHeight = codeHeight;
			mCodeText.setMinWidth(mCodeTextMinWidth);
			mCodeText.setMinHeight(mCodeTextMinHeight);
			invalidate();
		}
		super.onDraw(canvas);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		 mCodeText.postInvalidate();
	}

	public CodeText getCodeText() {
		return mCodeText;
	}

}
