package org.mushare.httper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

/**
 * Created by dklap on 4/30/2017.
 */

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
//        toolbar.inflateMenu(R.menu.menu_result_activity);

        ImageButton buttonShowInfo = (ImageButton) findViewById(R.id.buttonShowInfo);
        CheatSheet.setup(buttonShowInfo);
    }

}
