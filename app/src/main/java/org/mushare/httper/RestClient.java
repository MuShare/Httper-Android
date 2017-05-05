package org.mushare.httper;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dklap on 5/3/2017.
 */

public class RestClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, Header[] headers, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        client.get(context, url, headers, params, responseHandler);
    }

    public static void post(Context context, String url, Header[] headers, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(context, url, headers, params, null, responseHandler);
    }

    public static void cancel(Context context) {
        client.cancelRequests(context, true);
    }
}
