package org.mushare.httper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResultPrettyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pretty_result, container, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = "";
            try {
                json = new String(bundle.getByteArray("content"));
                MyJSONObject jsonObject = new MyJSONObject(json);
                textView.setText(jsonObject.getCharSequence(2));
            } catch (JSONException e) {
                textView.setText(json);
            }
        }
        return view;
    }
}
