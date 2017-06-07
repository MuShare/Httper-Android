package org.mushare.httper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by dklap on 6/7/2017.
 */

public class AboutActivity extends AppCompatActivity {
    ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        scrollView = (ScrollView) findViewById(R.id.scrollViewAbout);
        findViewById(R.id.imageButtonLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://httper" +
                        ".mushare.cn/"));
                startActivity(browserIntent);
            }
        });
        findViewById(R.id.imageButtonGithub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github" +
                        ".com/MuShare/Httper-Android"));
                startActivity(browserIntent);
            }
        });
        findViewById(R.id.imageButtonStore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                            + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google"
                            + ".com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutAbout);
        final String[] licenses = getResources().getStringArray(R.array.licenses);
        if (licenses.length % 3 == 0) {
            int i = 0;
            while (i < licenses.length) {
                LinearLayout item = (LinearLayout) LinearLayout.inflate(this, R.layout
                        .list_license_item, null);
                ((TextView) item.findViewById(R.id.license_name)).setText(licenses[i++]);
                ((TextView) item.findViewById(R.id.license_detail)).setText(licenses[i++]);
                final String url = licenses[i++];
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                });
                linearLayout.addView(item);
            }
        }
        if (savedInstanceState != null) {
            scrollView.scrollTo(0, savedInstanceState.getInt("scroll_position"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scroll_position", scrollView.getScrollY());
    }
}
