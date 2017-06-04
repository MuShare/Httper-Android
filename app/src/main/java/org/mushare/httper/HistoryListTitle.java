package org.mushare.httper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dklap on 6/2/2017.
 */

public class HistoryListTitle extends AbstractItem<HistoryListTitle, HistoryListTitle
        .MyViewHolder> implements Serializable {
    private String date;

    public HistoryListTitle(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return R.id.history_list_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_history_title;
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view);
    }

    @Override
    public void bindView(MyViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.textViewDate.setText(date);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;

        MyViewHolder(View view) {
            super(view);
            textViewDate = (TextView) view.findViewById(R.id.historyDate);
        }
    }
}
