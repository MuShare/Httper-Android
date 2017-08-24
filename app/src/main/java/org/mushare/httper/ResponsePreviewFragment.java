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

public class ResponsePreviewFragment extends Fragment {
    WebView webview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        webview = new WebView(getContext());
        webview.setInitialScale(100);
        WebSettings webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
//        if (savedInstanceState != null)
//            webview.restoreState(savedInstanceState);
//        else
        if (((ResponseActivity) getActivity()).responseBody != null) {
            try {
                webview.loadDataWithBaseURL(((ResponseActivity) getActivity()).url, (
                        (ResponseActivity) getActivity()).responseBody, null, null, null);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return webview;
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        webview.saveState(outState);
//    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        webview.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        webview.onResume();
        super.onResume();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (webview != null) {
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }
}
