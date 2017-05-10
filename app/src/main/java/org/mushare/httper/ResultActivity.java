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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    static String url;
    static byte[] responseBody;
    final int DIALOG_ERROR_CONNECT = 0;

    RequestParams params;
    Header[] headers;
    String method;

    File cacheFile;
    boolean refreshing;
    int statusCode;
    CharSequence responseHeaders;

    MyTouchableLinearLayout toolbar;
    ViewPager viewPager;
    TextView textViewStatusCode;
    TextView textViewHeader;
    View refreshView;
    BottomSheetBehavior bottomSheetBehavior;

    TextView textViewURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        toolbar = (MyTouchableLinearLayout) findViewById(R.id.appBar);

        textViewURL = (TextView) findViewById(R.id.textViewURL);
        textViewStatusCode = (TextView) findViewById(R.id.textViewStatusCode);
        textViewHeader = (TextView) findViewById(R.id.textViewHeaders);

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
        textViewURL.setText(url);
        params = new RequestParams((HashMap) intent.getSerializableExtra("parameter"));
        HashMap<String, String> headerMap = (HashMap) intent.getSerializableExtra("header");
        List<Header> headerList = new ArrayList<>();
        Set<String> keys = headerMap.keySet();
        for (String key : keys) {
            headerList.add(new BasicHeader(key, headerMap.get(key)));
        }
        headers = headerList.toArray(new Header[headerList.size()]);
        method = intent.getStringExtra("method");

        String cacheFilePath;
        if (savedInstanceState == null || (cacheFilePath = savedInstanceState.getString
                ("cacheFilePath")) == null || (refreshing = savedInstanceState.getBoolean
                ("refreshing")) || (statusCode = savedInstanceState.getInt("statusCode", -1)) ==
                -1 || (responseHeaders = savedInstanceState.getCharSequence("responseHeaders")) ==
                null) {
            cacheFile = new File(getCacheDir(), "response_cache_" + System.currentTimeMillis());
            refresh();
        } else {
            cacheFile = new File(cacheFilePath);
            if (cacheFile.exists()) {
                int size = (int) cacheFile.length();
                responseBody = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream
                            (cacheFile));
                    buf.read(responseBody, 0, responseBody.length);
                    buf.close();
                } catch (Exception ignored) {
                }
                MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(pagerAdapter);
                textViewStatusCode.setText(String.valueOf(statusCode));
                textViewHeader.setText(responseHeaders);
            }
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        ViewCompat.setElevation(bottomSheet, TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        if (savedInstanceState != null)
            bottomSheetBehavior.setState(savedInstanceState.getInt("bottomSheetState"));

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
            case "HEAD":
                RestClient.head(this, url, headers, params, new MyAsyncHttpResponseHandler());
                break;
            case "PUT":
                RestClient.put(this, url, headers, params, new MyAsyncHttpResponseHandler());
                break;
            case "DELETE":
                RestClient.delete(this, url, headers, params, new MyAsyncHttpResponseHandler());
                break;
            case "PATCH":
                RestClient.patch(this, url, headers, params, new MyAsyncHttpResponseHandler());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        RestClient.cancel(this);
        url = null;
        responseBody = null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cacheFilePath", cacheFile.getAbsolutePath());
        outState.putBoolean("refreshing", refreshing);
        outState.putInt("bottomSheetState", bottomSheetBehavior.getState());
        outState.putInt("statusCode", statusCode);
        outState.putCharSequence("responseHeaders", responseHeaders);
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
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        }).create();
    }

//    private CharSequence[] headersToCharSequences(Header[] headers) {
//        CharSequence[] out = new CharSequence[headers.length];
//        for (int i = 0; i < headers.length; i++) {
//            Spannable header = new SpannableString(headers[i].getName() + ": " + headers[i]
//                    .getValue());
//            header.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
//                    headers[i].getName().length() + 2, header.length(), Spannable
//                            .SPAN_EXCLUSIVE_EXCLUSIVE);
//            out[i] = header;
//        }
//        return out;
//    }

    private CharSequence headersToCharSequence(Header[] headers) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (Header header : headers) {
            spannableStringBuilder.append(header.getName());
            spannableStringBuilder.append(": ");
            Spannable value = new SpannableString(header.getValue());
            value.setSpan(new ForegroundColorSpan(getResources().getColor(R.color
                    .colorTextSecondary)), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(value);
            spannableStringBuilder.append("\n");
        }
        spannableStringBuilder.delete(spannableStringBuilder.length() - 1, spannableStringBuilder
                .length());
        return spannableStringBuilder;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new ResultPrettyFragment();
                case 1:
                    return new ResultRawFragment();
                case 2:
                    return new ResultPreviewFragment();
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
            ResultActivity.responseBody = responseBody;
            ResultActivity.this.statusCode = statusCode;
            responseHeaders = headersToCharSequence(headers);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream
                        (cacheFile));
                bos.write(responseBody);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            textViewStatusCode.setText(String.valueOf(statusCode));
            textViewHeader.setText(responseHeaders);
            refreshView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            toolbar.setAlpha(1f);
            toolbar.touchable(true);
            refreshing = false;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                              Throwable error) {
            ResultActivity.responseBody = responseBody;
            ResultActivity.this.statusCode = statusCode;
            responseHeaders = headersToCharSequence(headers);
            if (responseBody != null) {
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream
                            (cacheFile));
                    bos.write(responseBody);
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(pagerAdapter);
                textViewStatusCode.setText(String.valueOf(statusCode));
                textViewHeader.setText(responseHeaders);
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
