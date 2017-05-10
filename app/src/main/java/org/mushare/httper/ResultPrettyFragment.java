package org.mushare.httper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResultPrettyFragment extends Fragment {
    static final int MSG_DONE = 0;

    ArrayList<CharSequence> texts;
    View refreshingView;
    ListView listView;

    MyHandler myHandler = new MyHandler(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pretty_result, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        refreshingView = view.findViewById(R.id.refreshingView);

        new Thread() {
            @Override
            public void run() {
                if (texts == null) {
                    if (ResultActivity.responseBody != null) {
                        String text = new String(ResultActivity.responseBody);
                        try {
                            MyJSONObject jsonObject = new MyJSONObject(text);
                            texts = jsonObject.getCharSequences(2);
                        } catch (JSONException e) {
                            try {
                                MyJSONArray jsonArray = new MyJSONArray(text);
                                texts = jsonArray.getCharSequences(2);
                            } catch (JSONException e1) {
                                texts = new ArrayList<>();
                                texts.addAll(Arrays.asList(text.split("\n")));
                            }
                        }
                    }
                }
                myHandler.sendEmptyMessage(MSG_DONE);
            }
        }.start();

        return view;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ResultPrettyFragment> mFragment;

        MyHandler(ResultPrettyFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_DONE) {
                ResultPrettyFragment fragment = mFragment.get();
                if (fragment != null) {
                    fragment.refreshingView.setVisibility(View.GONE);
                    fragment.listView.setAdapter(new ArrayAdapter<>(fragment.getContext(), R
                            .layout.fragment_result_textview, fragment.texts));
                }
            }
        }
    }
}
