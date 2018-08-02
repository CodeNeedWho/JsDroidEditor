package com.jsdroid.editor;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    PreformEdit preformEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CodePane codePane = new CodePane(this);
        setContentView(codePane);
        String source = "function main(){ print(\"hello jsdroid!\"); } function main(){ print(\"hello jsdroid!\"); } function main(){ print(\"hello jsdroid!\"); } function main(){ print(\"hello jsdroid!\"); } function main(){ print(\"hello jsdroid!\"); } \n";
        source = source.replace("\t", "    ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            builder.append(source);
        }
        preformEdit = new PreformEdit(codePane.getCodeText());
        //10万文字，轻松高亮！
        preformEdit.setDefaultText(builder);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("撤销");
        menu.add("重做");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("撤销".equals(item.getTitle())) {
            if (preformEdit != null) {
                preformEdit.undo();
            }
        }
        if ("重做".equals(item.getTitle())) {
            if (preformEdit != null) {
                preformEdit.redo();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
