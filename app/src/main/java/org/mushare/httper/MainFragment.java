package org.mushare.httper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;
import org.mushare.httper.entity.DaoSession;
import org.mushare.httper.entity.RequestRecord;
import org.mushare.httper.entity.RequestRecordDao;
import org.mushare.httper.utils.MyApp;

import java.util.ArrayList;
import java.util.HashMap;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.ISectionable;

/**
 * Created by dklap on 5/22/2017.
 */

public class MainFragment extends Fragment {
    FlexibleAdapter<HttpSettingListItem> adapter;
    RequestRecordDao requestRecordDao;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaoSession daoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
        requestRecordDao = daoSession.getRequestRecordDao();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        final Spinner spinnerMethod = (Spinner) view.findViewById(R.id.spinnerMethods);
        final Spinner spinnerHttp = (Spinner) view.findViewById(R.id.spinnerHttp);
        final EditText editTextUrl = (EditText) view.findViewById(R.id.editTextUrl);
        final Button buttonSend = (Button) view.findViewById(R.id.buttonSend);

        editTextUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && s.toString().matches("^" +
                        // user:pass authentication
                        "(?:\\S+(?::\\S*)?@)?" +
                        "(?:" +
                        // IP address exclusion
                        // private & local networks
                        "(?!(?:10|127)(?:\\.\\d{1,3}){3})" +
                        "(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
                        "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})" +
                        // IP address dotted notation octets
                        // excludes loopback network 0.0.0.0
                        // excludes reserved space >= 224.0.0.0
                        // excludes network & broacast addresses
                        // (first & last IP address of each class)
                        "(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
                        "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}" +
                        "(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
                        "|" +
                        // host name
                        "(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)" +
                        // domain name
                        "(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*" +
                        // TLD identifier
                        "(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))" +
                        // TLD may end with dot
                        "\\.?" +
                        ")" +
                        // port number
                        "(?::\\d{2,5})?" +
                        // resource path
                        "(?:[/?#]\\S*)?" +
                        "$")) buttonSend.setEnabled(true);
                else buttonSend.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState == null) {
            ArrayList<HttpSettingListItem> dataSet = new ArrayList<>();
            dataSet.add(new HttpSettingListItem(HttpSettingListTitle.getInstance
                    (HttpSettingListTitle
                            .TYPE_HEADER)));
            dataSet.add(new HttpSettingListItem(HttpSettingListTitle.getInstance
                    (HttpSettingListTitle
                            .TYPE_PARAMETER)));
            adapter = new FlexibleAdapter<>(dataSet);
            adapter.setDisplayHeadersAtStartUp(true).setStickyHeaders(true);
        } else restoreAdapter(savedInstanceState);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    InputMethodManager keyboard = (InputMethodManager) recyclerView
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestRecord requestRecord = new RequestRecord();
                requestRecord.setCreateAt(System.currentTimeMillis());
                requestRecord.setMethod(spinnerMethod.getSelectedItem().toString());
                requestRecord.setHttp(spinnerHttp.getSelectedItem().toString());
                requestRecord.setUrl(editTextUrl.getText().toString());
                requestRecord.setHeaders(new JSONObject(getHeaders()).toString());
                requestRecord.setParameters(new JSONObject(getParameters()).toString());
                requestRecordDao.insert(requestRecord);

                Intent intent = new Intent(getContext(), ResultActivity.class);
                intent.putExtra("method", spinnerMethod.getSelectedItem().toString());
                intent.putExtra("http", spinnerHttp.getSelectedItem().toString());
                intent.putExtra("url", editTextUrl.getText().toString());
                intent.putExtra("header", getHeaders());
                intent.putExtra("parameter", getParameters());
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main_fragment);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return true;
            }
        });

        return view;
    }

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> header = new HashMap<>();
        for (ISectionable sectionable : adapter.getSectionItems(HttpSettingListTitle.getInstance
                (HttpSettingListTitle.TYPE_HEADER))) {
            HttpSettingListItem item = (HttpSettingListItem) sectionable;
            String key;
            if ((key = item.getKey()) != null && !key.isEmpty()) {
                String value = item.getValue();
                header.put(key, value == null ? "" : value);
            }
        }
        return header;
    }

    public HashMap<String, String> getParameters() {
        HashMap<String, String> param = new HashMap<>();
        for (ISectionable sectionable : adapter.getSectionItems(HttpSettingListTitle.getInstance
                (HttpSettingListTitle.TYPE_PARAMETER))) {
            HttpSettingListItem item = (HttpSettingListItem) sectionable;
            String key;
            if ((key = item.getKey()) != null && !key.isEmpty()) {
                String value = item.getValue();
                param.put(key, value == null ? "" : value);
            }
        }
        return param;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveAdapter(outState);
    }

    private void saveAdapter(Bundle outState) {
        ArrayList<ISectionable> dataSet = new ArrayList<>();
        dataSet.addAll(adapter.getSectionItems(HttpSettingListTitle.getInstance(HttpSettingListTitle
                .TYPE_HEADER)));
        dataSet.addAll(adapter.getSectionItems(HttpSettingListTitle.getInstance(HttpSettingListTitle
                .TYPE_PARAMETER)));
        outState.putSerializable("dataSet", dataSet);
        adapter.onSaveInstanceState(outState);
    }

    private void restoreAdapter(Bundle savedInstanceState) {
        ArrayList<HttpSettingListItem> dataSet = (ArrayList<HttpSettingListItem>)
                savedInstanceState.getSerializable("dataSet");
        adapter = new FlexibleAdapter<>(dataSet);
        adapter.onRestoreInstanceState(savedInstanceState);
    }
}
