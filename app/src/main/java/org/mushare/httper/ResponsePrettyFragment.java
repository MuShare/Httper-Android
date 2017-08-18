package org.mushare.httper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.mushare.httper.utils.MyJSONArray;
import org.mushare.httper.utils.MyJSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dklap on 5/4/2017.
 */
public class ResponsePrettyFragment extends Fragment {
    ArrayList<CharSequence> texts;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response_listview, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        if (((ResponseActivity) getActivity()).responseBody != null) {
            try {
                MyJSONObject jsonObject = new MyJSONObject(((ResponseActivity) getActivity())
                        .responseBody);
                texts = jsonObject.getCharSequences(2);
            } catch (JSONException e) {
                try {
                    MyJSONArray jsonArray = new MyJSONArray(((ResponseActivity) getActivity())
                            .responseBody);
                    texts = jsonArray.getCharSequences(2);
                } catch (JSONException e1) {
                    texts = new ArrayList<>();
                    texts.addAll(Arrays.asList(((ResponseActivity) getActivity()).responseBody.split
                            ("\n")));
                }
            }
            listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_response_textview,
                    texts));
        }
        return view;
    }
}
