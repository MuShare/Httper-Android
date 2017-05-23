package org.mushare.httper;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by dklap on 5/21/2017.
 */

public class HttpSettingListTitle extends AbstractHeaderItem<HttpSettingListTitle.MyViewHolder> {
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_PARAMETER = 2;
    private static SparseArray<HttpSettingListTitle> instances = new SparseArray<>();
    private int type;

    private HttpSettingListTitle(int type) {
        this.type = type;
    }

    public static HttpSettingListTitle getInstance(int type) {
        HttpSettingListTitle instance;
        if ((instance = instances.get(type)) == null) {
            instance = new HttpSettingListTitle(type);
            instances.put(type, instance);
        }
        return instance;
    }

    public int getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HttpSettingListTitle && type == ((HttpSettingListTitle) o).type;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.http_setting_title;
    }

    @Override
    public MyViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                         ViewGroup parent) {
        return new MyViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, MyViewHolder holder, int position,
                               List payloads) {
        switch (type) {
            case HttpSettingListTitle.TYPE_HEADER:
                holder.textViewTitle.setText(R.string.main_view_headers);
                break;
            case HttpSettingListTitle.TYPE_PARAMETER:
                holder.textViewTitle.setText(R.string.main_view_params);
        }
        holder.imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpSettingListItem childItem = new HttpSettingListItem(HttpSettingListTitle.this);
                adapter.addItemToSection(childItem, HttpSettingListTitle.this, adapter
                        .getSectionItems(HttpSettingListTitle.this).size());
            }
        });
    }


    static class MyViewHolder extends FlexibleViewHolder {
        TextView textViewTitle;
        ImageButton imageButtonAdd;

        MyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            imageButtonAdd = (ImageButton) view.findViewById(R.id.imageButtonAdd);
        }
    }
}
