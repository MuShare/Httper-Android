package org.mushare.httper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResponseRawFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response_listview, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        if (((ResponseActivity) getActivity()).responseBody != null) {
            String[] texts = ((ResponseActivity) getActivity()).responseBody.split("\n");
            listView.setAdapter(new ArrayAdapter<>(getContext(), R
                    .layout.list_response_textview, texts));
        }
        return view;
    }
}
