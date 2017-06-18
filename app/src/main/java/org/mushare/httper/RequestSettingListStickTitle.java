package org.mushare.httper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import org.mushare.httper.utils.RequestSettingDataUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dklap on 5/21/2017.
 */

public class RequestSettingListStickTitle extends
        AbstractRequestSettingListItem<RequestSettingListStickTitle, RequestSettingListStickTitle
                .MyViewHolder> implements Serializable {

    public RequestSettingListStickTitle(RequestSettingType requestSettingType) {
        super(requestSettingType);
    }

    public RequestSettingType getRequestSettingType() {
        return requestSettingType;
    }

    @Override
    public int getType() {
        return R.id.request_setting_list_title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.http_setting_title;
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view);
    }

    @Override
    public void bindView(MyViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        switch (requestSettingType) {
            case header:
                holder.textViewTitle.setText(R.string.main_view_headers);
                holder.imageButtonAdd.setVisibility(View.VISIBLE);
                break;
            case parameter:
                holder.textViewTitle.setText(R.string.main_view_params);
                holder.imageButtonAdd.setVisibility(View.VISIBLE);
                break;
            case body:
                holder.textViewTitle.setText(R.string.main_view_body);
                holder.imageButtonAdd.setVisibility(View.INVISIBLE);
                break;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageButton imageButtonAdd;

        MyViewHolder(View view) {
            super(view);
            textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            imageButtonAdd = (ImageButton) view.findViewById(R.id.imageButtonAdd);
        }
    }

    public static class AddEvent extends ClickEventHook<AbstractRequestSettingListItem> {

        @Override
        public void onClick(View v, int position, FastAdapter<AbstractRequestSettingListItem>
                fastAdapter, AbstractRequestSettingListItem item) {
            RequestSettingType type = item.getRequestSettingType();
            if (type == RequestSettingType.body) return;
            FastItemAdapter<AbstractRequestSettingListItem> fastItemAdapter =
                    (FastItemAdapter<AbstractRequestSettingListItem>) fastAdapter;
            fastItemAdapter.add(RequestSettingDataUtils.lastIndexOf(fastItemAdapter
                    .getAdapterItems(), type) + 1, new RequestSettingListKVItem(type));
        }

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RequestSettingListStickTitle.MyViewHolder) {
                return ((MyViewHolder) viewHolder).imageButtonAdd;
            }
            return null;
        }
    }
}