package org.mushare.httper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.mushare.httper.entity.DaoSession;
import org.mushare.httper.entity.RequestRecord;
import org.mushare.httper.entity.RequestRecordDao;
import org.mushare.httper.utils.MyApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.flexibleadapter.items.ISectionable;

/**
 * Created by dklap on 6/2/2017.
 */

public class RequestHistoryActivity extends AppCompatActivity {
    FlexibleAdapter<HistoryListItem> adapter;
    RequestRecordDao requestRecordDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaoSession daoSession = ((MyApp) getApplication()).getDaoSession();
        requestRecordDao = daoSession.getRequestRecordDao();

        setContentView(R.layout.activity_history);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState == null) {
            ArrayList<HistoryListItem> dataSet = new ArrayList<>();
            List<RequestRecord> requestRecordList = requestRecordDao.queryBuilder().orderDesc
                    (RequestRecordDao.Properties.CreateAt).build().list();
            for (RequestRecord requestRecord : requestRecordList) {
                dataSet.add(new HistoryListItem(HistoryListTitle.getInstance(DateFormat
                        .getMediumDateFormat(this).format(new Date(requestRecord.getCreateAt())))
                        , requestRecord.getMethod(), requestRecord.getHttp() + requestRecord
                        .getUrl(), requestRecord.getId()));
            }
            adapter = new FlexibleAdapter<>(dataSet);
            adapter.setDisplayHeadersAtStartUp(true);
        } else restoreAdapter(savedInstanceState);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveAdapter(outState);
    }

    private void saveAdapter(Bundle outState) {
        ArrayList<ISectionable> dataSet = new ArrayList<>();
        for (IHeader header : adapter.getHeaderItems())
            dataSet.addAll(adapter.getSectionItems(header));
        outState.putSerializable("dataSet", dataSet);
        adapter.onSaveInstanceState(outState);
    }

    private void restoreAdapter(Bundle savedInstanceState) {
        ArrayList<HistoryListItem> dataSet = (ArrayList<HistoryListItem>) savedInstanceState
                .getSerializable("dataSet");
        adapter = new FlexibleAdapter<>(dataSet);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HistoryListTitle.clearCache();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemClickEvent(ItemClickEvent event) {
        Intent intent = new Intent();
        intent.putExtra("requestRecordId", event.id);
        setResult(RESULT_OK, intent);
        finish();
    }

    static class ItemClickEvent {
        Long id;

        ItemClickEvent(long id) {
            this.id = id;
        }
    }
}
