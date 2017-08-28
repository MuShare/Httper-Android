package org.mushare.httper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.mushare.httper.utils.HttpUtils;
import org.mushare.httper.utils.MyPair;
import org.mushare.httper.utils.RestClient;
import org.mushare.httper.view.MyTouchableLinearLayout;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;

import static android.os.Build.VERSION.SDK_INT;
import static okhttp3.internal.Util.UTF_8;


/**
 * Created by dklap on 4/30/2017.
 */

public class ResponseActivity extends AppCompatActivity {
    static final int MSG_DONE = 0;
    static final int MSG_Fail = 1;
    final int DIALOG_ERROR_CONNECT = 0;
    List<CharSequence> responseBody;
    ArrayList<MyPair> params;
    ArrayList<MyPair> headers;
    String method;
    String url;
    String body;
    String charset;

    Call call;
    MyHandler myHandler = new MyHandler(this);

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
        if (SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            findViewById(R.id.main).setPadding(0, getStatusBarHeight(), 0, 0);
        }
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
        params = (ArrayList<MyPair>) intent.getSerializableExtra("parameter");
        url = HttpUtils.combineUrl(url, params);
        headers = (ArrayList<MyPair>) intent.getSerializableExtra("header");
        method = intent.getStringExtra("method");
        body = intent.getStringExtra("body");

        cacheFile = new File(getCacheDir(), "response_cache");
        if (savedInstanceState == null || (refreshing = savedInstanceState.getBoolean
                ("refreshing"))) {
            cacheFile.delete();
            refresh();
        } else if (cacheFile.exists()) {
            charset = savedInstanceState.getString("charset");
            try {
                responseBody = loadCache(charset);
            } catch (IOException ignored) {
            }
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            url = savedInstanceState.getString("url");
            statusCode = savedInstanceState.getInt("statusCode");
            responseHeaders = savedInstanceState.getCharSequence("responseHeaders");
            textViewURL.setText(url);
            textViewStatusCode.setText(String.valueOf(statusCode));
            textViewHeader.setText(responseHeaders);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        ViewCompat.setElevation(bottomSheet, TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        if (savedInstanceState != null)
            bottomSheetBehavior.setState(savedInstanceState.getInt("bottomSheetState"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_result_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        });
        final View dimBackground = findViewById(R.id.dimBackground);
        dimBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    dimBackground.setVisibility(View.GONE);
                    textViewHeader.clearFocus();
                    textViewURL.clearFocus();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                dimBackground.setVisibility(View.VISIBLE);
                dimBackground.setAlpha(slideOffset);
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private List<CharSequence> loadCache(String charset) throws IOException {
        List<CharSequence> list = new LinkedList<>();
        BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream
                (cacheFile), charset));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = buf.readLine()) != null) {
            sb.append(line).append("\n");
            if (sb.length() > 1024) {
                sb.setLength(sb.length() - 1);
                list.add(sb.toString());
                sb.setLength(0);
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            list.add(sb.toString());
        }
        return list;
    }

    private void refresh() {
        refreshing = true;
        if (call != null) call.cancel();
        refreshView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        toolbar.setAlpha(0.4f);
        toolbar.touchable(false);
        switch (method) {
            case "GET":
                call = RestClient.get(url, headers, new MyCallback());
                break;
            case "POST":
                call = RestClient.post(url, headers, body, new MyCallback());
                break;
            case "HEAD":
                call = RestClient.head(url, headers, new MyCallback());
                break;
            case "PUT":
                call = RestClient.put(url, headers, body, new MyCallback());
                break;
            case "DELETE":
                call = RestClient.delete(url, headers, body, new MyCallback());
                break;
            case "PATCH":
                call = RestClient.patch(url, headers, body, new MyCallback());
                break;
        }
    }

    void refreshFinish() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        textViewURL.setText(url);
        textViewStatusCode.setText(String.valueOf(statusCode));
        textViewHeader.setText(responseHeaders);
        refreshView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        toolbar.setAlpha(1f);
        toolbar.touchable(true);
    }

    void refreshFail(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("errorMessage", message);
        showDialog(DIALOG_ERROR_CONNECT, bundle);
        refreshView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        toolbar.setAlpha(1f);
        toolbar.touchable(true);
    }

    @Override
    protected void onDestroy() {
        if (call != null) call.cancel();
        responseBody = null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("refreshing", refreshing);
        outState.putInt("bottomSheetState", bottomSheetBehavior.getState());
        outState.putInt("statusCode", statusCode);
        outState.putCharSequence("responseHeaders", responseHeaders);
        outState.putString("url", url);
        outState.putString("charset", charset);
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

    private CharSequence headersToCharSequence(Headers headers) {
        if (headers == null) return null;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (int i = 0, size = headers.size(); i < size; i++) {
            Spannable value = new SpannableString(headers.value(i));
            value.setSpan(new ForegroundColorSpan(getResources().getColor(R.color
                    .colorTextSecondary)), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(headers.name(i)).append(": ").append(value).append("\n");
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

    private static class MyHandler extends Handler {
        private final WeakReference<ResponseActivity> mFragment;

        MyHandler(ResponseActivity fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DONE) {
                ResponseActivity fragment = mFragment.get();
                if (fragment != null) {
                    fragment.refreshFinish();
                }
            } else if (msg.what == MSG_Fail) {
                ResponseActivity fragment = mFragment.get();
                if (fragment != null) {
                    fragment.refreshFail(msg.getData().getString("error"));
                }
            }
        }
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
                    return new ResponsePrettyFragment();
                case 1:
                    return new ResponseRawFragment();
                case 2:
                    return new ResponsePreviewFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private class MyCallback implements Callback {

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            if (call.isCanceled()) return;
            refreshing = false;
            Message message = new Message();
            message.what = MSG_Fail;
            Bundle bundle = new Bundle();
            bundle.putString("error", e.getMessage());
            message.setData(bundle);
            myHandler.sendMessage(message);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            ResponseBody body = response.body();
            if (body != null) {
                try {
                    MediaType mediaType = body.contentType();
//                    Log.i("mediaType", mediaType != null ? mediaType.toString() : "");
                    charset = Util.bomAwareCharset(body.source(), mediaType != null ? mediaType
                            .charset(UTF_8) : UTF_8).displayName();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream
                            (cacheFile));
                    byte[] buffer = new byte[8 * 1024];
                    int bytesRead;
                    while ((bytesRead = body.byteStream().read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    bos.flush();
                    bos.close();
                    body.close();
                    responseBody = loadCache(charset);
                } catch (IOException ignored) {
                }
            }
            url = response.request().url().toString();
            statusCode = response.code();
            responseHeaders = headersToCharSequence(response.headers());

            refreshing = false;
            myHandler.sendEmptyMessage(MSG_DONE);
        }
    }
}
