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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jsdroid.editor.R;

/**
 * Created by Administrator on 2018/2/11.
 */

@SuppressLint("AppCompatCustomView")
public class CodeText extends EditText {

	public final static int MAX_TEXT_LEN = 30000;
	// 高亮颜色数组
	protected int colors[];
	// 文字范围
	protected ScrollView scrollView;
	
	// 最大行号：工具\n数量得到
	int maxLineNumble;

	public CodeText(Context context) {
		super(context);
		init();

	}

	public CodeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void init() {
		colors = new int[MAX_TEXT_LEN];
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		// 设置布局
		setGravity(Gravity.START);
		// 设值最多字符串
		InputFilter[] filters = { new InputFilter.LengthFilter(MAX_TEXT_LEN) };
		setFilters(filters);
		// 设值背景透明
		setBackgroundColor(Color.TRANSPARENT);
		// 设值字体大小
		setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		// 设置字体颜色
		setTextColor(Color.TRANSPARENT);
		// 设置字体
		setTypeface(Typeface.MONOSPACE);
		// 设置光标颜色
		setCursorColor();
		addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() >= MAX_TEXT_LEN) {
					Toast.makeText(getContext(), "文字上限:" + MAX_TEXT_LEN,
							Toast.LENGTH_LONG).show();
				}
				JsParserThread.parser(CodeText.this);
			}
		});
	}

	// 反射设置光标颜色
	private void setCursorColor() {
		try {// 修改光标的颜色（反射）
			Field mCursorDrawableResField = TextView.class
					.getDeclaredField("mCursorDrawableRes");
			mCursorDrawableResField.setAccessible(true);
			mCursorDrawableResField.set(this,R.drawable.shape_edit_cursor_color);
		} catch (Exception ignored) {
		}
	}

	// 计算行文字
	public Map<Integer, String> getLineNumbles() {
		Map<Integer, String> maps = new HashMap<>();
		Layout layout = getLayout();
		if (layout == null) {
			return maps;
		}
		int lineNumble = 1;
		maps.put(0, Integer.toString(lineNumble));
		int lineCount = getLineCount();
		for (int i = 1; i < lineCount; i++) {
			int charPos = layout.getLineStart(i);
			if (getText().charAt(charPos - 1) == '\n') {
				lineNumble++;
				maps.put(i, Integer.toString(lineNumble));
				maxLineNumble = lineNumble;
			}
		}
		return maps;
	}

	// 解析需要绘制的首行号
	int unpackRangeStartFromLong(long range) {
		return (int) (range >>> 32);
	}

	// 解析需要绘制的尾行号
	int unpackRangeEndFromLong(long range) {
		return (int) (range & 0x00000000FFFFFFFFL);
	}

	Method getLineRangeForDrawMethod;

	long getLineRangeForDraw(Layout layout, Canvas canvas)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		if (getLineRangeForDrawMethod == null) {
			getLineRangeForDrawMethod = Layout.class.getMethod(
					"getLineRangeForDraw", Canvas.class);
			getLineRangeForDrawMethod.setAccessible(true);
		}
		return (long) getLineRangeForDrawMethod.invoke(layout, canvas);
	}

	Method getUpdatedHighlightPathMthod;

	Path getUpdatedHighlightPath() throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {
		if (getUpdatedHighlightPathMthod == null) {
			getUpdatedHighlightPathMthod = TextView.class
					.getDeclaredMethod("getUpdatedHighlightPath");
			getUpdatedHighlightPathMthod.setAccessible(true);
		}
		return (Path) getUpdatedHighlightPathMthod.invoke(this);
	}

	Method drawBackgroundMthod;

	void drawBackground(Canvas canvas, Path highlight, Paint highlightPaint,
			int cursorOffsetVertical, int firstLine, int lastLine)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		if (drawBackgroundMthod == null) {
			drawBackgroundMthod = Layout.class.getDeclaredMethod(
					"drawBackground", Canvas.class, Path.class, Paint.class,
					int.class, int.class, int.class);
			drawBackgroundMthod.setAccessible(true);
		}
		if (getLayout() != null)
			drawBackgroundMthod.invoke(getLayout(), canvas, highlight,
					highlightPaint, cursorOffsetVertical, firstLine, lastLine);
	}

	Field mHighlightPaintField;

	Paint getHighlightPaint() throws IllegalAccessException,
			NoSuchFieldException {
		if (mHighlightPaintField == null) {
			mHighlightPaintField = TextView.class
					.getDeclaredField("mHighlightPaint");
			mHighlightPaintField.setAccessible(true);
		}
		return (Paint) mHighlightPaintField.get(this);
	}

	Field mEditorField;

	Object getEditor() throws NoSuchFieldException, IllegalAccessException {
		if (mEditorField == null) {
			mEditorField = TextView.class.getDeclaredField("mEditor");
			mEditorField.setAccessible(true);
		}
		return mEditorField.get(this);
	}

	Field mCursorCountField;

	int getCusorCount() throws NoSuchFieldException, IllegalAccessException {
		if (mCursorCountField == null) {
			mCursorCountField = getEditor().getClass().getDeclaredField(
					"mCursorCount");
			mCursorCountField.setAccessible(true);
		}
		return (int) mCursorCountField.get(getEditor());
	}

	Method drawCursorMethod;

	void drawCursor(Canvas canvas, int cursorOffsetVertical)
			throws NoSuchFieldException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (drawCursorMethod == null) {
			drawBackgroundMthod = getEditor().getClass().getDeclaredMethod(
					"drawCursor", Canvas.class, int.class);
			drawBackgroundMthod.setAccessible(true);
		}
		drawBackgroundMthod.invoke(getEditor(), canvas, cursorOffsetVertical);
	}

	Method assumeLayoutMethod;

	void assumeLayout() throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {
		if (assumeLayoutMethod == null) {
			assumeLayoutMethod = TextView.class
					.getDeclaredMethod("assumeLayout");
			assumeLayoutMethod.setAccessible(true);
		}
		assumeLayoutMethod.invoke(this);
	}

	void superDraw(Canvas canvas) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {
		if (getLayout() == null) {
			assumeLayout();
		}
		final int selectionStart = getSelectionStart();
		final int selectionEnd = getSelectionEnd();
		Path highlight = getUpdatedHighlightPath();
		Paint mHighlightPaint = getHighlightPaint();

		final int cursorOffsetVertical = 0;
		canvas.save();
		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
		if (highlight != null) {
			canvas.drawPath(highlight, mHighlightPaint);
		}
		if (highlight != null && selectionStart == selectionEnd
				&& getCusorCount() > 0) {
			drawCursor(canvas, cursorOffsetVertical);
		}
		canvas.restore();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 根据行号计算左边距padding
		String max = Integer.toString(maxLineNumble);
		float lineNumbleSize = getPaint().measureText(max) + 20;
		if (getPaddingLeft() != lineNumbleSize) {
			setPadding((int) lineNumbleSize, 0, 0, 0);
			invalidate();
		}
		try {
			// 优化速度绘制背景(选择高亮，光标闪动)
			superDraw(canvas);
		} catch (Exception e) {
			// 默认绘制背景
			super.onDraw(canvas);
		}
		// 画文字
		canvas.save();
		canvas.translate(0, getExtendedPaddingTop());
		drawText(canvas);
		canvas.restore();

	}

	// 获取选择行，如果多余一行，返回-1
	public int getSelectLine() {
		Layout layout = getLayout();
		if (getSelectionStart() != getSelectionEnd()) {
			return -1;
		}
		return layout.getLineForOffset(getSelectionStart());
	}

	// 绘制文本着色
	private void drawText(Canvas canvas) {
		long lineRange = 0;
		Layout layout = getLayout();
		int selectLine = getSelectLine();
		{// 计算需要绘制的行号所需要的linerage
			canvas.save();
			float clipTop = (scrollView.getScrollY() == 0) ? 0
					: getExtendedPaddingTop() + scrollView.getScrollY()
							- scrollView.getPaddingTop();
			canvas.clipRect(0, clipTop, getWidth(), scrollView.getScrollY()
					+ scrollView.getHeight());
			try {
				lineRange = getLineRangeForDraw(layout, canvas);
			} catch (NoSuchMethodException e) {
			} catch (InvocationTargetException e) {
			} catch (IllegalAccessException e) {
			}
			canvas.restore();
		}
		int firstLine = unpackRangeStartFromLong(lineRange);
		int lastLine = unpackRangeEndFromLong(lineRange);
		if (firstLine < 0) {
			return;
		}
		int previousLineBottom = layout.getLineTop(firstLine);
		int previousLineEnd = layout.getLineStart(firstLine);
		int lineCount = getLineCount();
		Paint paint = getPaint();

		Map<Integer, String> lineNumbles = getLineNumbles();
		for (int lineNum = firstLine; lineNum <= lastLine
				&& lineNum < lineCount; lineNum++) {
			int start = previousLineEnd;
			if (start >= MAX_TEXT_LEN) {
				break;
			}
			previousLineEnd = layout.getLineStart(lineNum + 1);
			int end = layout.getLineVisibleEnd(lineNum);

			int ltop = previousLineBottom;
			int lbottom = layout.getLineTop(lineNum + 1);
			previousLineBottom = lbottom;
			int lbaseline = lbottom - layout.getLineDescent(lineNum);

			int left = getPaddingLeft();

			// 绘制选择行背景
			if (lineNum == selectLine) {
				paint.setColor(0x33ffffff);
				canvas.drawRect(0, ltop, getPaddingLeft(), lbottom, paint);
			}
			// 绘制行号
			String lineNumbleText = lineNumbles.get(lineNum);
			if (lineNumbleText != null) {

				paint.setColor(0xffffffff);
				canvas.drawText(lineNumbleText, 0, lineNumbleText.length(), 10,
						lbaseline, paint);

			}

			int fontCount = 0;
			int lastColor = colors[start];

			int lastColorPos = start;
			// 绘制文字
			for (int i = start; i < end; i++) {
				fontCount++;
				int color = colors[i];
				if (lastColor != color) {
					{// 绘制文字
						if (lastColor == 0) {
							lastColor = 0xff00ff00;
						}
						paint.setColor(lastColor);
						float offsetX = paint.measureText(getText(), start,
								lastColorPos);
						canvas.drawText(getText(), lastColorPos, lastColorPos
								+ fontCount, left + offsetX, lbaseline, paint);
					}
					lastColor = color;
					lastColorPos = i;
					fontCount = 1;
				}
				// 如果是行内最后一个字符
				if (i == end - 1) {
					{// 绘制文字
						if (lastColor == 0) {
							lastColor = 0xff00ff00;
						}
						paint.setColor(lastColor);
						float offsetX = paint.measureText(getText(), start,
								lastColorPos);
						canvas.drawText(getText(), lastColorPos, lastColorPos
								+ fontCount, left + offsetX, lbaseline, paint);
					}
				}
			}

		}

	}

	public int[] getCodeColors() {
		return colors;
	}

}
