package org.mushare.httper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by dklap on 5/4/2017.
 */

public class MyJSONObject extends JSONObject {
    public MyJSONObject(String json) throws JSONException {
        super(new JSONTokener(json));
    }

    public ArrayList<CharSequence> getCharSequences(int indentSpaces) throws JSONException {
        MyJSONStringer stringer = new MyJSONStringer(indentSpaces);
        writeTo(stringer);
        return stringer.getCharSequences();
    }

    private void writeTo(MyJSONStringer stringer) throws JSONException {
        stringer.object();
        Iterator<String> keys = keys();
        while (keys.hasNext()) {
            String key = keys.next();
            stringer.key(key).value(opt(key));
        }
        stringer.endObject();
        stringer.preGetCharSequences();
    }
}
