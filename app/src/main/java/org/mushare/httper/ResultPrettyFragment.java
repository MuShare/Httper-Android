package org.mushare.httper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResultPrettyFragment extends Fragment {
    CharSequence text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pretty_result, container, false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        Bundle bundle = getArguments();
        if (text == null && bundle != null) {
            text = new String(bundle.getByteArray("content"));
            try {
                MyJSONObject jsonObject = new MyJSONObject(text.toString());
                text = jsonObject.getCharSequence(2);
            } catch (JSONException e) {
            }
        }
        textView.setText(text);
        return view;
    }
}
