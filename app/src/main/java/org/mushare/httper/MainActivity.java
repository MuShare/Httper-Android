package org.mushare.httper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArrayList<HttpSettingListItem> dataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner spinnerMethod = (Spinner) findViewById(R.id.spinnerMethods);
        final Spinner spinnerHttp = (Spinner) findViewById(R.id.spinnerHttp);
        final EditText editTextUrl = (EditText) findViewById(R.id.editTextUrl);
        final Button buttonSend = (Button) findViewById(R.id.buttonSend);

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState == null || savedInstanceState.getSerializable("dataSet") == null) {
            dataSet = new ArrayList<>();
            dataSet.add(new HttpSettingListItem(HttpSettingListItem.TYPE_HEADER_TITLE));
            dataSet.add(new HttpSettingListItemPair(HttpSettingListItem.TYPE_HEADER));
            dataSet.add(new HttpSettingListItem(HttpSettingListItem.TYPE_PARAMETER_TITLE));
            dataSet.add(new HttpSettingListItemPair(HttpSettingListItem.TYPE_PARAMETER));
        } else {
            dataSet = (ArrayList<HttpSettingListItem>) savedInstanceState.getSerializable
                    ("dataSet");
        }

        // specify an adapter (see also next example)
        HttpSettingListAdapter adapter = new HttpSettingListAdapter(dataSet);
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("method", spinnerMethod.getSelectedItem().toString());
                intent.putExtra("http", spinnerHttp.getSelectedItem().toString());
                intent.putExtra("url", editTextUrl.getText().toString());
                intent.putExtra("header", getHeaders());
                intent.putExtra("parameter", getParameters());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("dataSet", dataSet);
    }

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> header = new HashMap<>();
        for (HttpSettingListItem item : dataSet) {
            if (item.getType() != HttpSettingListItem.TYPE_HEADER) continue;
            String key;
            if ((key = ((HttpSettingListItemPair) item).getKey()) != null && !key.isEmpty()) {
                String value = ((HttpSettingListItemPair) item).getValue();
                header.put(key, value == null ? "" : value);
            }
        }
        return header;
    }

    public HashMap<String, String> getParameters() {
        HashMap<String, String> param = new HashMap<>();
        for (HttpSettingListItem item : dataSet) {
            if (item.getType() != HttpSettingListItem.TYPE_PARAMETER) continue;
            String key;
            if ((key = ((HttpSettingListItemPair) item).getKey()) != null && !key.isEmpty()) {
                String value = ((HttpSettingListItemPair) item).getValue();
                param.put(key, value == null ? "" : value);
            }
        }
        return param;
    }
}
