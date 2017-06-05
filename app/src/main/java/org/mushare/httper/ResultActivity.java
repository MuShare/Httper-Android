package org.mushare.httper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by dklap on 4/30/2017.
 */

public class ResultActivity extends AppCompatActivity {
    static final int MSG_DONE = 0;
    static final int MSG_Fail = 1;
    static String responseBody;
    final int DIALOG_ERROR_CONNECT = 0;
    ArrayList<MyPair> params;
    ArrayList<MyPair> headers;
    String method;
    String url;

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
        textViewURL.setText(url);
        headers = (ArrayList<MyPair>) intent.getSerializableExtra("header");
        method = intent.getStringExtra("method");

        cacheFile = new File(getCacheDir(), "response_cache");
        if (savedInstanceState == null || (refreshing = savedInstanceState.getBoolean
                ("refreshing"))) {
            cacheFile.delete();
            refresh();
        } else if (cacheFile.exists()) {
            try {
                BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream
                        (cacheFile)));
                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = buf.readLine();
                }
                responseBody = sb.toString();
                buf.close();
            } catch (Exception ignored) {
            }
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            statusCode = savedInstanceState.getInt("statusCode");
            responseHeaders = savedInstanceState.getCharSequence("responseHeaders");
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
                call = RestClient.post(url, headers, null, new MyCallback());
                break;
            case "HEAD":
                call = RestClient.head(url, headers, new MyCallback());
                break;
            case "PUT":
                call = RestClient.put(url, headers, null, new MyCallback());
                break;
            case "DELETE":
                call = RestClient.delete(url, headers, null, new MyCallback());
                break;
            case "PATCH":
                call = RestClient.patch(url, headers, null, new MyCallback());
                break;
        }
    }

    void refreshFinish() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
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

//    private CharSequence headersToCharSequence(Header[] headers) {
//        if (headers == null) return null;
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//        for (Header header : headers) {
//            spannableStringBuilder.append(header.getName());
//            spannableStringBuilder.append(": ");
//            Spannable value = new SpannableString(header.getValue());
//            value.setSpan(new ForegroundColorSpan(getResources().getColor(R.color
//                    .colorTextSecondary)), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannableStringBuilder.append(value);
//            spannableStringBuilder.append("\n");
//        }
//        spannableStringBuilder.delete(spannableStringBuilder.length() - 1, spannableStringBuilder
//                .length());
//        return spannableStringBuilder;
//    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ResultActivity> mFragment;

        MyHandler(ResultActivity fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DONE) {
                ResultActivity fragment = mFragment.get();
                if (fragment != null) {
                    fragment.refreshFinish();
                }
            } else if (msg.what == MSG_Fail) {
                ResultActivity fragment = mFragment.get();
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
                    return new ResultPrettyFragment();
                case 1:
                    return new ResultRawFragment();
                case 2:
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    return Fragment.instantiate(ResultActivity.this, ResultPreviewFragment.class
                            .getName(), bundle);
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
            ResultActivity.responseBody = response.body().string();
            ResultActivity.this.statusCode = response.code();
            responseHeaders = response.headers().toString();
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream
                        (cacheFile));
                bos.write(responseBody.getBytes());
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            refreshing = false;
            myHandler.sendEmptyMessage(MSG_DONE);
        }
    }
}
