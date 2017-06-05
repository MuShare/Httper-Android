package org.mushare.httper.utils;

import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * Created by dklap on 6/5/2017.
 */

public class HttpUtils {
    public static Headers phaseHeaders(List<MyPair> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        for (MyPair keyAndValue : headers) {
            headerBuilder.add(keyAndValue.getFirst(), keyAndValue.getSecond());
        }
        return headerBuilder.build();
    }

    public static String combineUrl(String url, List<MyPair> parameters) {
        if (parameters == null || parameters.size() == 0) return url;
        List<String> list = new ArrayList<>();
        for (MyPair keyAndValue : parameters) {
            list.add(Uri.encode(keyAndValue.getFirst(), null) + "=" + Uri.encode(keyAndValue
                    .getSecond(), null));
        }
        if (url.indexOf('?') > -1) return url + '&' + TextUtils.join("&", list);
        else return url + '?' + TextUtils.join("&", list);
    }

    public static JSONArray pairListToJSONArray(List<MyPair> list) {
        List<String> newList = new ArrayList<>();
        for (MyPair myPair : list) {
            newList.add(myPair.getFirst());
            newList.add(myPair.getSecond());
        }
        return new JSONArray(newList);
    }

    public static List<MyPair> jsonArrayToPairList(JSONArray jsonArray) throws JSONException {
        List<MyPair> list = new ArrayList<>();
        int i = 0, j = jsonArray.length();
        while (i < j) {
            MyPair myPair = new MyPair(jsonArray.getString(i++), jsonArray.getString(i++));
            list.add(myPair);
        }
        return list;
    }
}
