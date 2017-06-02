package org.mushare.httper;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by dklap on 4/28/2017.
 */

public class HistoryListItem extends AbstractSectionableItem<HistoryListItem
        .MyViewHolder, HistoryListTitle> implements Parcelable {
    public static final Creator<HistoryListItem> CREATOR = new Creator<HistoryListItem>() {
        @Override
        public HistoryListItem createFromParcel(Parcel source) {
            return new HistoryListItem(HistoryListTitle.getInstance(source.readString()),
                    source.readString(), source.readString(), source.readLong());
        }

        @Override
        public HistoryListItem[] newArray(int size) {
            return new HistoryListItem[size];
        }
    };
    private String method;
    private String url;
    private long id;

    HistoryListItem(HistoryListTitle header, String method, String url, long id) {
        super(header);
        this.method = method;
        this.url = url;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HistoryListItem && id == ((HistoryListItem) o).id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_history_item;
    }

    @Override
    public MyViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                         ViewGroup parent) {
        return new MyViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, final MyViewHolder holder, int
            position, List payloads) {
        holder.textViewMethod.setText(method);
        holder.textViewUrl.setText(url);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RequestHistoryActivity.ItemClickEvent(id));
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header.date);
        dest.writeString(method);
        dest.writeString(url);
        dest.writeLong(id);
    }

    static class MyViewHolder extends FlexibleViewHolder {
        TextView textViewMethod;
        TextView textViewUrl;

        MyViewHolder(View view, final FlexibleAdapter adapter) {
            super(view, adapter);
            textViewMethod = (TextView) view.findViewById(R.id.method);
            textViewUrl = (TextView) view.findViewById(R.id.url);
        }
    }
}
