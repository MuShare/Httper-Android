package org.mushare.httper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

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
    final int ERROR_CODE_ERROR_UNKNOWN = -1;
    final int ERROR_CODE_ERROR_HOST = 0;
    final int ERROR_CODE_ERROR_SSL = 1;

    String url;
    RequestParams params;
    Header[] headers;
    String method;
    byte[] responseBody;

    MyTouchableLinearLayout toolbar;
    ViewPager viewPager;
    View refreshing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        toolbar = (MyTouchableLinearLayout) findViewById(R.id.appBar);

        ImageButton buttonShowInfo = (ImageButton) findViewById(R.id.buttonShowInfo);
        CheatSheet.setup(buttonShowInfo);

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
        refreshing = findViewById(R.id.refreshingView);

        Intent intent = getIntent();
        url = intent.getStringExtra("http") + intent.getStringExtra("url");
        params = new RequestParams((HashMap) intent.getSerializableExtra("parameter"));
        HashMap<String, String> headerMap = (HashMap) intent.getSerializableExtra("header");
        List<Header> headerList = new ArrayList<>();
        Set<String> keys = headerMap.keySet();
        for (String key : keys) {
            headerList.add(new BasicHeader(key, headerMap.get(key)));
        }
        headers = headerList.toArray(new Header[headerList.size()]);
        method = intent.getStringExtra("method");

        if (savedInstanceState == null) {
            refresh();
        } else {
            responseBody = savedInstanceState.getByteArray("responseBody");
            MyPagerAdapter pagerAdapter = new MyPagerAdapter
                    (getSupportFragmentManager(), url, responseBody);

            viewPager.setAdapter(pagerAdapter);
        }
    }

    private void refresh() {
        refreshing.setVisibility(View.VISIBLE);
        toolbar.setAlpha(0.4f);
        toolbar.touchable(false);
        switch (method) {
            case "GET":
                RestClient.get(url, headers, params, new MyAsyncHttpResponseHandler());
                break;
            case "POST":
                RestClient.post(url, headers, params, new MyAsyncHttpResponseHandler());
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray("responseBody", responseBody);
    }

    @Nullable
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        String msg;
        switch (args.getInt("errorCode")) {
            case ERROR_CODE_ERROR_HOST:
                msg = getString(R.string.error_unknown_host);
                break;
            case ERROR_CODE_ERROR_SSL:
                msg = getString(R.string.error_ssl);
                break;
            default:
                msg = getString(R.string.error_unknown);
                break;
        }
        return new AlertDialog.Builder(this).setMessage(msg).setPositiveButton(getString(R.string
                .dialog_ok), null).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        }).create();
    }

//    @Override
//    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
//        String msg;
//        switch (args.getInt("errorCode")) {
//            case ERROR_CODE_ERROR_HOST:
//                msg = getString(R.string.error_unknown_host);
//                break;
//            case ERROR_CODE_ERROR_SSL:
//                msg = getString(R.string.error_ssl);
//                break;
//            default:
//                msg = getString(R.string.error_unknown);
//                break;
//        }
//        ((AlertDialog) dialog).setMessage(msg);
//    }

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
            refreshing.setVisibility(View.GONE);
            toolbar.setAlpha(1f);
            toolbar.touchable(true);
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
                String msg = error.getMessage();
                if (msg.startsWith("UnknownHostException exception"))
                    bundle.putInt("errorCode", ERROR_CODE_ERROR_HOST);
                else if (msg.startsWith("hostname in certificate didn't match"))
                    bundle.putInt("errorCode", ERROR_CODE_ERROR_SSL);
                else bundle.putInt("errorCode", ERROR_CODE_ERROR_UNKNOWN);
                if (!isFinishing()) showDialog(DIALOG_ERROR_CONNECT, bundle);
            }
            refreshing.setVisibility(View.GONE);
            toolbar.setAlpha(1f);
            toolbar.touchable(true);
        }
    }
}
