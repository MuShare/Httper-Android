package org.mushare.httper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.lang.ref.WeakReference;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResultPrettyFragment extends Fragment {
    static final int MSG_DONE = 0;

    CharSequence text;
    View refreshingView;
    TextView textView;

    MyHandler myHandler = new MyHandler(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pretty_result, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
        refreshingView = view.findViewById(R.id.refreshingView);

        new Thread() {
            @Override
            public void run() {
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
                myHandler.sendEmptyMessage(MSG_DONE);
            }
        }.start();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("text", text);
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
                    fragment.textView.setText(fragment.text);
                }
            }
        }
    }
}
