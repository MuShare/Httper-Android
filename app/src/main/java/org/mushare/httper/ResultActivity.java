package org.mushare.httper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Created by dklap on 4/30/2017.
 */

public class ResultActivity extends AppCompatActivity {
    final int DIALOG_ERROR_CONNECT = 0;

    String url;
    RequestParams params;
    Header[] headers;
    String method;
    byte[] responseBody;
    boolean refreshing;

    MyTouchableLinearLayout toolbar;
    ViewPager viewPager;
    View refreshView;
    BottomSheetBehavior bottomSheetBehavior;

    TextView responseURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        toolbar = (MyTouchableLinearLayout) findViewById(R.id.appBar);

        responseURL = (TextView) findViewById(R.id.textViewURL);

        viewPager = (ViewPager) findViewById(R.id.content);
        viewPager.setOffscreenPageLimit(2);

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
        refreshView = findViewById(R.id.refreshingView);

        Intent intent = getIntent();
        url = intent.getStringExtra("http") + intent.getStringExtra("url");
        responseURL.setText(url);
        params = new RequestParams((HashMap) intent.getSerializableExtra("parameter"));
        HashMap<String, String> headerMap = (HashMap) intent.getSerializableExtra("header");
        List<Header> headerList = new ArrayList<>();
        Set<String> keys = headerMap.keySet();
        for (String key : keys) {
            headerList.add(new BasicHeader(key, headerMap.get(key)));
        }
        headers = headerList.toArray(new Header[headerList.size()]);
        method = intent.getStringExtra("method");

        if (savedInstanceState == null || (refreshing = savedInstanceState.getBoolean
                ("refreshing"))) {
            refresh();
        } else {
            responseBody = savedInstanceState.getByteArray("responseBody");
            MyPagerAdapter pagerAdapter = new MyPagerAdapter
                    (getSupportFragmentManager(), url, responseBody);

            viewPager.setAdapter(pagerAdapter);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        ViewCompat.setElevation(bottomSheet, TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        ImageButton buttonShowInfo = (ImageButton) findViewById(R.id.buttonShowInfo);
        CheatSheet.setup(buttonShowInfo);
        buttonShowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void refresh() {
        refreshing = true;
        RestClient.cancel(this);
        refreshView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        toolbar.setAlpha(0.4f);
        toolbar.touchable(false);
        switch (method) {
            case "GET":
                RestClient.get(this, url, headers, params, new MyAsyncHttpResponseHandler());
                break;
            case "POST":
                RestClient.post(this, url, headers, params, new MyAsyncHttpResponseHandler());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        RestClient.cancel(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray("responseBody", responseBody);
        outState.putBoolean("refreshing", refreshing);
    }

    @Nullable
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        return new AlertDialog.Builder(this).setTitle(R.string.dialog_error).setMessage(getString
                (R.string.error, args.getString("errorMessage"))).setPositiveButton(getString(R
                .string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).create();
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

    private class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            ResultActivity.this.responseBody = responseBody;
            MyPagerAdapter pagerAdapter = new MyPagerAdapter
                    (getSupportFragmentManager(), url, responseBody);
            viewPager.setAdapter(pagerAdapter);
            refreshView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            toolbar.setAlpha(1f);
            toolbar.touchable(true);
            refreshing = false;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                              Throwable error) {
            ResultActivity.this.responseBody = responseBody;
            if (responseBody != null) {
                MyPagerAdapter pagerAdapter = new MyPagerAdapter
                        (getSupportFragmentManager(), url, responseBody);
                viewPager.setAdapter(pagerAdapter);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("errorMessage", error.getMessage());
                showDialog(DIALOG_ERROR_CONNECT, bundle);
            }
            refreshView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            toolbar.setAlpha(1f);
            toolbar.touchable(true);
            refreshing = false;
        }
    }
}
