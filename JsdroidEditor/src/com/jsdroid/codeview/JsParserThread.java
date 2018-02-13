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

/**
 * Created by Administrator on 2018/2/12.
 */

public class JsParserThread extends java.lang.Thread {
    private static JsParserThread jsParserThread;

    CodeText codeText;
    boolean running;

    private JsParserThread(CodeText codeText) {
        this.codeText = codeText;
        running=true;
    }


    @Override
    public void run() {
		TokenStream ts = new TokenStream(null, sourceString, 0);
		while(running){
            try {
                int token = ts.getToken();
                if(token == Token.EOF){
                    break;
                }
                int color = Token.getColor(token);
                for(int i=ts.getTokenBeg();i<=ts.getTokenEnd();i++){
                    colors[i] = color;
                }
            } catch (Exception e) {
            }
		}
        try {
            codeText.postInvalidate();
        } catch (Exception e) {
        }

    }

    public synchronized static void parser(CodeText codeText) {
        if (jsParserThread != null) {
            jsParserThread.stopParser();
        }
        jsParserThread = new JsParserThread(codeText);
        jsParserThread.start();
    }

    public void stopParser() {
        running = false;
    }
}
