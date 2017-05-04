package org.mushare.httper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.content);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent intent = getIntent();
        final String url = intent.getStringExtra("http") + intent.getStringExtra("url");
        switch (intent.getStringExtra("method")) {
            case "GET":
                RestClient.get(url, new
                        RequestParams((HashMap) intent.getSerializableExtra("parameter")), new
                        AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[]
                                    responseBody) {
                                MyPagerAdapter pagerAdapter = new MyPagerAdapter
                                        (getSupportFragmentManager(), url, responseBody);

                                viewPager.setAdapter(pagerAdapter);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[]
                                    responseBody,
                                                  Throwable error) {
                                MyPagerAdapter pagerAdapter = new MyPagerAdapter
                                        (getSupportFragmentManager(), url, responseBody);

                                viewPager.setAdapter(pagerAdapter);
                            }
                        });
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        byte[] content;
        String baseUrl;

        MyPagerAdapter(FragmentManager fm, String baseUrl, byte[] content) {
            super(fm);
            this.baseUrl = baseUrl;
            this.content = content;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle bundle = new Bundle();
            bundle.putByteArray("content", content);
            bundle.putString("baseUrl", baseUrl);
            switch (position) {
                case 0:
                    ResultPrettyFragment resultPrettyFragment = new ResultPrettyFragment();
                    resultPrettyFragment.setArguments(bundle);
                    return resultPrettyFragment;
                case 1:
                    ResultRawFragment resultRawFragment = new ResultRawFragment();
                    resultRawFragment.setArguments(bundle);
                    return resultRawFragment;
                case 2:
                    ResultPreviewFragment resultPreviewFragment = new ResultPreviewFragment();
                    resultPreviewFragment.setArguments(bundle);
                    return resultPreviewFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
