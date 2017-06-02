package org.mushare.httper.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

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

    public static void head(Context context, String url, Header[] headers, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.head(context, url, headers, params, responseHandler);
    }

    public static void put(Context context, String url, Header[] headers, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        try {
            client.put(context, url, headers, params.getEntity(responseHandler), null,
                    responseHandler);
        } catch (IOException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, e);
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void delete(Context context, String url, Header[] headers, RequestParams params,
                              AsyncHttpResponseHandler responseHandler) {
        client.delete(context, url, headers, params, responseHandler);
    }

    public static void patch(Context context, String url, Header[] headers, RequestParams params,
                             AsyncHttpResponseHandler responseHandler) {
        try {
            client.patch(context, url, headers, params.getEntity(responseHandler), null,
                    responseHandler);
        } catch (IOException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, e);
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void cancel(Context context) {
        client.cancelRequests(context, true);
    }
}
