package org.mushare.httper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by dklap on 5/4/2017.
 */

public class MyJSONArray extends JSONArray {
    public MyJSONArray(String json) throws JSONException {
        super(new JSONTokener(json));
    }

    public ArrayList<CharSequence> getCharSequences(int indentSpaces) throws JSONException {
        MyJSONStringer stringer = new MyJSONStringer(indentSpaces);
        writeTo(stringer);
        return stringer.getCharSequences();
    }

    private void writeTo(MyJSONStringer stringer) throws JSONException {
        stringer.array();
        for (int i = 0, l = length(); i < l; i++) {
            Object object = opt(i);
            stringer.value(object);
        }
        stringer.endArray();
        stringer.preGetCharSequences();
    }
}
