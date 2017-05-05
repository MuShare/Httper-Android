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

        if (savedInstanceState != null) {
            text = savedInstanceState.getCharSequence("text");
        }
        if (text == null) {
            Bundle bundle = getArguments();
            byte[] data;
            if (bundle != null && (data = bundle.getByteArray("content")) != null) {
                text = new String(data);
                try {
                    MyJSONObject jsonObject = new MyJSONObject(text.toString());
                    text = jsonObject.getCharSequence(2);
                } catch (JSONException e) {
                    try {
                        MyJSONArray jsonArray = new MyJSONArray(text.toString());
                        text = jsonArray.getCharSequence(2);
                    } catch (JSONException e1) {
                    }
                }
            }
        }
        textView.setText(text);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("text", text);
    }
}
