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

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.mushare.httper.entity.DaoSession;
import org.mushare.httper.entity.RequestRecord;
import org.mushare.httper.entity.RequestRecordDao;
import org.mushare.httper.utils.MyApp;
import org.mushare.httper.utils.RequestSettingDataUtils;
import org.mushare.httper.view.MyStickyHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dklap on 5/22/2017.
 */

public class MainFragment extends Fragment {
    final int HISTORY_CODE = 0;
    FastItemAdapter<IItem> adapter;
    RequestRecordDao requestRecordDao;

    Spinner spinnerMethod;
    Spinner spinnerHttp;
    EditText editTextUrl;

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
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        spinnerMethod = (Spinner) view.findViewById(R.id.spinnerMethods);
        spinnerHttp = (Spinner) view.findViewById(R.id.spinnerHttp);
        editTextUrl = (EditText) view.findViewById(R.id.editTextUrl);
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

        //create our adapters
        adapter = new FastItemAdapter<>();

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        adapter.setHasStableIds(true);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if (savedInstanceState == null) {
            adapter.add(new RequestSettingListStickTitle(RequestSettingType.header), new
                    RequestSettingListKVItem(RequestSettingType.header), new
                    RequestSettingListStickTitle(RequestSettingType.parameter), new
                    RequestSettingListKVItem(RequestSettingType.parameter));
        } else restoreAdapter(savedInstanceState);

        adapter.withEventHook(new RequestSettingListStickTitle.AddEvent()).withEventHook(new
                RequestSettingListKVItem.RemoveEvent());

        final MyStickyHeader stickyHeader = (MyStickyHeader) view.findViewById(R.id.stickyHeader);
        stickyHeader.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestSettingType type = stickyHeader.getType();
                adapter.add(RequestSettingDataUtils.lastIndexOf(adapter.getAdapterItems(), type)
                        + 1, new RequestSettingListKVItem(type));
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    InputMethodManager keyboard = (InputMethodManager) recyclerView
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int index = RequestSettingDataUtils.lastIndexOf(adapter.getAdapterItems(),
                        RequestSettingType.header) + 1;
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
                if (position > index) {
                    stickyHeader.setType(RequestSettingType.parameter);
                    stickyHeader.setTranslationY(0);
                } else if (position < index) {
                    stickyHeader.setType(RequestSettingType.header);
                    stickyHeader.setTranslationY(0);
                } else {
                    stickyHeader.setType(RequestSettingType.header);
                    View title = recyclerView.getLayoutManager().findViewByPosition(position);
                    float distance = title.getY() - stickyHeader.getY();
                    int stickyHeaderHeight = stickyHeader.getHeight();
                    float stickyHeaderTranslationY = stickyHeader.getTranslationY();
                    if (distance < stickyHeaderHeight || stickyHeaderTranslationY < 0)
                        stickyHeader.setTranslationY(Math.min(stickyHeaderTranslationY + distance -
                                stickyHeaderHeight, 0));
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
                startActivityForResult(new Intent(getContext(), RequestHistoryActivity.class),
                        HISTORY_CODE);
                return true;
            }
        });

        return view;
    }

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> header = new HashMap<>();
        for (IItem iItem : adapter.getAdapterItems()) {
            RequestSettingListKVItem item;
            if (iItem instanceof RequestSettingListKVItem && (item = (RequestSettingListKVItem)
                    iItem).getRequestSettingType() == RequestSettingType.header) {
                String key;
                if ((key = item.getKey()) != null && !key.isEmpty()) {
                    String value = item.getValue();
                    header.put(key, value == null ? "" : value);
                }
            }
        }
        return header;
    }

    public HashMap<String, String> getParameters() {
        HashMap<String, String> param = new HashMap<>();
        for (IItem iItem : adapter.getAdapterItems()) {
            RequestSettingListKVItem item;
            if (iItem instanceof RequestSettingListKVItem && (item = (RequestSettingListKVItem)
                    iItem).getRequestSettingType() == RequestSettingType.parameter) {
                String key;
                if ((key = item.getKey()) != null && !key.isEmpty()) {
                    String value = item.getValue();
                    param.put(key, value == null ? "" : value);
                }
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
        outState = adapter.saveInstanceState(outState);
        outState.putSerializable("dataSet", new ArrayList<>(adapter.getAdapterItems()));
    }

    private void restoreAdapter(Bundle savedInstanceState) {
        adapter.set((ArrayList<IItem>) savedInstanceState.getSerializable("dataSet"));
        adapter.withSavedInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HISTORY_CODE && resultCode == RESULT_OK) {
            RequestRecord requestRecord = requestRecordDao.queryBuilder().where(RequestRecordDao
                    .Properties.Id.eq(data.getLongExtra("requestRecordId", -1L))).build().unique();
            if (requestRecord == null) return;
            int spinnerMethodSelection = indexOfStringInArray(getResources().getStringArray(R.array
                    .methods_array), requestRecord.getMethod());
            int spinnerHttpSelection = indexOfStringInArray(getResources().getStringArray(R.array
                    .http_array), requestRecord.getHttp());
            if (spinnerHttpSelection == -1 || spinnerMethodSelection == -1) return;
            ArrayList<IItem> dataSet = new ArrayList<>();
            try {
                JSONObject header = new JSONObject(requestRecord.getHeaders());
                JSONObject parameter = new JSONObject(requestRecord.getParameters());
                dataSet.add(new RequestSettingListStickTitle(RequestSettingType.header));
                if (header.length() == 0)
                    dataSet.add(new RequestSettingListKVItem(RequestSettingType.header));
                else {
                    Iterator<String> keys = header.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        dataSet.add(new RequestSettingListKVItem(RequestSettingType.header, key,
                                header.getString(key)));
                    }
                }
                dataSet.add(new RequestSettingListStickTitle(RequestSettingType.parameter));
                if (parameter.length() == 0)
                    dataSet.add(new RequestSettingListKVItem(RequestSettingType.parameter));
                else {
                    Iterator<String> keys = parameter.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        dataSet.add(new RequestSettingListKVItem(RequestSettingType.parameter,
                                key, parameter.getString(key)));
                    }
                }
            } catch (JSONException e) {
                return;
            }
            spinnerMethod.setSelection(spinnerMethodSelection);
            spinnerHttp.setSelection(spinnerHttpSelection);
            editTextUrl.setText(requestRecord.getUrl());
            adapter.set(dataSet);
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    int indexOfStringInArray(String[] array, String s) {
        if (array == null || s == null) return -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(s)) return i;
        }
        return -1;
    }
}
