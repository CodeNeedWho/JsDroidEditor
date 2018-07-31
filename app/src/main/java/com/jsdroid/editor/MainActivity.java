package com.jsdroid.editor;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    PreformEdit preformEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CodePane codePane = new CodePane(this);
        String source = "function main(){\n\tprint(\"hello jsdroid!\");\n}";
        source = source.replace("\t", "    ");
        preformEdit = new PreformEdit(codePane.getCodeText());
        preformEdit.setDefaultText(source);
        setContentView(codePane);
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
