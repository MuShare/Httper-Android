package org.mushare.httper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;

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
    final int CLEAR_HISTORY_DIALOG_ID = 0;
    FlexibleAdapter<HistoryListItem> adapter;
    RequestRecordDao requestRecordDao;

    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaoSession daoSession = ((MyApp) getApplication()).getDaoSession();
        requestRecordDao = daoSession.getRequestRecordDao();

        setContentView(R.layout.activity_history);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_history_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showDialog(CLEAR_HISTORY_DIALOG_ID);
                return true;
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(this).setTitle(R.string.dialog_warn).setMessage(R.string
                .history_warn).setPositiveButton(R.string.dialog_ok, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestRecordDao.deleteAll();
                adapter = new FlexibleAdapter<>(new ArrayList<HistoryListItem>());
                recyclerView.swapAdapter(adapter, false);
            }
        }).setNegativeButton(R.string.dialog_cancel, null).create();
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
