package org.mushare.httper;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dklap on 4/28/2017.
 */

public class HistoryListItem extends AbstractItem<HistoryListItem, HistoryListItem.MyViewHolder>
        implements Serializable {
    private String method;
    private String url;
    private long id;

    HistoryListItem(String method, String url, long id) {
        this.method = method;
        this.url = url;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int getType() {
        return R.id.history_list_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_history_item;
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view);
    }

    @Override
    public void bindView(MyViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.textViewMethod.setText(method);
        holder.textViewUrl.setText(url);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMethod;
        TextView textViewUrl;

        MyViewHolder(View view) {
            super(view);
            textViewMethod = view.findViewById(R.id.method);
            textViewUrl = view.findViewById(R.id.url);
        }
    }
}
