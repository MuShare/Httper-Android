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
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.mushare.httper.entity.DaoSession;
import org.mushare.httper.entity.RequestRecord;
import org.mushare.httper.entity.RequestRecordDao;
import org.mushare.httper.utils.MyApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dklap on 6/2/2017.
 */

public class RequestHistoryActivity extends AppCompatActivity {
    final int CLEAR_HISTORY_DIALOG_ID = 0;
    RequestRecordDao requestRecordDao;
    Toolbar toolbar;
    View emptyView;
    private FastItemAdapter<IItem> itemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaoSession daoSession = ((MyApp) getApplication()).getDaoSession();
        requestRecordDao = daoSession.getRequestRecordDao();

        setContentView(R.layout.activity_history);

        //create our adapters
        itemAdapter = new FastItemAdapter<>();

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        itemAdapter.setHasStableIds(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(50);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAdapter);
        if (savedInstanceState == null) {
            ArrayList<IItem> dataSet = new ArrayList<>();
            List<RequestRecord> requestRecordList = requestRecordDao.queryBuilder().orderDesc
                    (RequestRecordDao.Properties.CreateAt).build().list();
            String lastDate = null;
            for (RequestRecord requestRecord : requestRecordList) {
                String date = DateFormat.getMediumDateFormat(this).format(new Date(requestRecord
                        .getCreateAt()));
                if (!date.equals(lastDate)) {
                    dataSet.add(new HistoryListTitle(date));
                    lastDate = date;
                }
                dataSet.add(new HistoryListItem(requestRecord.getMethod(), requestRecord.getHttp
                        () + requestRecord.getUrl(), requestRecord.getId()));
            }
            itemAdapter.set(dataSet);
        } else restoreAdapter(savedInstanceState);
        itemAdapter.withOnClickListener(new FastAdapter.OnClickListener<IItem>() {
            @Override
            public boolean onClick(View v, IAdapter<IItem> adapter, IItem item, int position) {
                if (item instanceof HistoryListItem) {
                    Intent intent = new Intent();
                    intent.putExtra("requestRecordId", ((HistoryListItem) item).getId());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return false;
            }
        });


        emptyView = findViewById(R.id.emptyMessage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_history_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showDialog(CLEAR_HISTORY_DIALOG_ID);
                return true;
            }
        });

        if (itemAdapter.getAdapterItemCount() == 0)
            toolbar.getMenu().findItem(R.id.menuClearHistory).setVisible(false);
        else emptyView.setVisibility(View.GONE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(this).setTitle(R.string.dialog_warn).setMessage(R.string
                .history_warn).setPositiveButton(R.string.dialog_ok, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestRecordDao.deleteAll();
                itemAdapter.clear();
                emptyView.setVisibility(View.VISIBLE);
                toolbar.getMenu().findItem(R.id.menuClearHistory).setVisible(false);
            }
        }).setNegativeButton(R.string.dialog_cancel, null).create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveAdapter(outState);
    }

    private void saveAdapter(Bundle outState) {
        outState = itemAdapter.saveInstanceState(outState);
        ArrayList<IItem> dataSet = new ArrayList<>(itemAdapter.getAdapterItems());
        outState.putSerializable("dataSet", dataSet);
    }

    private void restoreAdapter(Bundle savedInstanceState) {
        ArrayList<IItem> dataSet = (ArrayList<IItem>) savedInstanceState.getSerializable("dataSet");
        itemAdapter.set(dataSet);
        itemAdapter.withSavedInstanceState(savedInstanceState);
    }
}
