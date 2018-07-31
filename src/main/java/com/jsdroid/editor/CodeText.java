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


package com.jsdroid.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by Administrator on 2018/2/11.
 */

public class CodeText extends ColorsText {
    JsCodeParser codeParser;

    public CodeText(Context context) {
        super(context);
        init();

    }

    public CodeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        codeParser = new JsCodeParser(this);
        // 动态解析js代码更新文字颜色
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                codeParser.parse(start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private long drawUseTimeCount = 0;
    private long lastEnterTime = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        super.onDraw(canvas);
        long endTime = System.currentTimeMillis();
        drawUseTimeCount = endTime - startTime;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_TAB) {
            int start = getSelectionStart();
            int end = getSelectionEnd();
            if (start == end) {
                getText().insert(start, "    ");
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //防止在反应慢的设备上一次换三行
            if (System.currentTimeMillis() - lastEnterTime < drawUseTimeCount*3) {
                return true;
            }
            lastEnterTime = System.currentTimeMillis();
            int start = getSelectionStart();
            int end = getSelectionEnd();
            if (start == end) {
                int line = getLayout().getLineForOffset(start);
                int startPos = getLayout().getLineStart(line);
                String space = "";
                for (int i = startPos; i < getText().length(); i++) {
                    if (getText().charAt(i) == ' ') {
                        space += " ";
                    } else {
                        break;
                    }
                }
                getText().insert(start, "\n" + space);
                if (start > 0) {
                    char c = getText().charAt(start - 1);
                    if (c == '{' || c == '[' || c == '(') {
                        start = getSelectionStart();
                        getText().insert(start, "    ");
                        start = getSelectionStart();
                        getText().insert(start, "\n" + space);
                        setSelection(start);
                    }
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
