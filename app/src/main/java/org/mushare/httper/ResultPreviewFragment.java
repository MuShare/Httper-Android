package org.mushare.httper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResultPreviewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        String url = getArguments() == null ? null : getArguments().getString("url");
        WebView webview = (WebView) inflater.inflate(R.layout.fragment_preview_result, container,
                false);
        webview.setInitialScale(100);
        WebSettings webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        if (ResultActivity.responseBody != null)
            webview.loadDataWithBaseURL(url, ResultActivity.responseBody, "text/html", null, null);
        return webview;
    }
}
