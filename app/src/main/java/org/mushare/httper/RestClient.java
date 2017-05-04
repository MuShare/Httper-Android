package org.mushare.httper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dklap on 5/3/2017.
 */

public class RestClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, Header[] headers, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        client.get(null, url, headers, params, responseHandler);
    }

    public static void post(String url, Header[] headers, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(null, url, headers, params, null, responseHandler);
    }
}
