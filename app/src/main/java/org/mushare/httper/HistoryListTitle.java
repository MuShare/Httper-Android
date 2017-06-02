package org.mushare.httper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by dklap on 6/2/2017.
 */

public class HistoryListTitle extends AbstractHeaderItem<HistoryListTitle.MyViewHolder> {
    private static Map<String, HistoryListTitle> instances = new HashMap<>();
    String date;

    private HistoryListTitle(String date) {
        this.date = date;
    }

    public static HistoryListTitle getInstance(String date) {
        HistoryListTitle instance;
        if ((instance = instances.get(date)) == null) {
            instance = new HistoryListTitle(date);
            instances.put(date, instance);
        }
        return instance;
    }

    public static void clearCache() {
        instances.clear();
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HistoryListTitle && date.equals(((HistoryListTitle) o).date);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_history_title;
    }

    @Override
    public MyViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                         ViewGroup parent) {
        return new MyViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, MyViewHolder holder, int position,
                               List payloads) {
        holder.textViewDate.setText(date);
    }


    static class MyViewHolder extends FlexibleViewHolder {
        TextView textViewDate;

        MyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            textViewDate = (TextView) view.findViewById(R.id.historyDate);
        }
    }
}
