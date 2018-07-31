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

public class JsParser {

    /**
     * 代码着色，将数组colors填充颜色即可。
     * 这里使用了rhino的代码解析器
     *
     * @param sourceString
     * @param colors
     */
    public static  void parserColor(String sourceString, int[] colors){

        TokenStream ts = new TokenStream(null, sourceString, 0);
        for (;;){
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

    }
}
