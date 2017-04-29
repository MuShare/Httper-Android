package org.mushare.httper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<HttpSettingListItem> dataSet = new LinkedList<>();
        dataSet.add(new HttpSettingListItem(HttpSettingListItem.TYPE_HEADER_TITLE));
        dataSet.add(new HttpSettingListItemPair(HttpSettingListItem.TYPE_HEADER));
        dataSet.add(new HttpSettingListItem(HttpSettingListItem.TYPE_PARAMETER_TITLE));
        dataSet.add(new HttpSettingListItemPair(HttpSettingListItem.TYPE_PARAMETER));

        // specify an adapter (see also next example)
        HttpSettingListAdapter adapter = new HttpSettingListAdapter(dataSet);
        recyclerView.setAdapter(adapter);
    }
}
