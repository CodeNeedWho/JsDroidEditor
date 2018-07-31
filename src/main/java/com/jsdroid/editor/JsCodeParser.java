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

import org.mozilla.javascript.Token;
import org.mozilla.javascript.TokenStream;

/**
 * Created by Administrator on 2018/2/12.
 */

public class JsCodeParser implements Runnable {
    static Object parserLock = new Object();
    boolean running;
    CodeText codeText;
    //start:改变的位置
    int start;
    //before:删除的数量
    int before;
    //count:添加的数量
    int count;

    public JsCodeParser(CodeText codeText) {
        this.codeText = codeText;
    }

    public void run() {
        synchronized (parserLock) {
            //颜色移动位置
            //start:改变的位置
            //before:删除的数量
            //count:添加的数量
            int[] lastColors = codeText.getCodeColors();
            if (lastColors != null) {
                int len = codeText.getText().length();
                int colors[] = new int[len];
                for (int i = 0; i < start && i < len && i < lastColors.length; i++) {
                    colors[i] = lastColors[i];
                }
                for (int i = start; i < colors.length; i++) {
                    int pos = (i - count + before);
                    if (pos < 0 || pos >= lastColors.length) {
                        break;
                    }
                    colors[i] = lastColors[pos];
                }
                codeText.setCodeColors(colors);

            } else {
                lastColors = new int[codeText.getText().length()];
                codeText.setCodeColors(lastColors);
            }
            codeText.postInvalidate();
            try {
                TokenStream ts = new TokenStream(null, codeText.getText()
                        .toString(), 0);
                int colors[] = new int[codeText.getText().length()];
                while (running) {
                    try {
                        int token = ts.getToken();
                        if (token == Token.EOF) {
                            codeText.setCodeColors(colors);
                            codeText.postInvalidate();
                            break;
                        }
                        int color = Token.getColor(token);
                        for (int i = ts.getTokenBeg(); i <= ts.getTokenEnd(); i++) {
                            colors[i] = color;
                        }
                    } catch (Exception e) {
                    }
                }

            } catch (Exception e) {
            }

            parserLock.notifyAll();
        }
        running = false;
    }

    public synchronized void parse(int start, int before,
                                   int count) {
        if (running) {
            stopParse();
        }
        running = true;
        this.start = start;
        this.before = before;
        this.count = count;
        new Thread(this).start();
    }

    public void stopParse() {
        running = false;
        synchronized (parserLock) {
            try {
                parserLock.wait(100);
            } catch (InterruptedException e) {
            }
        }
    }
}
