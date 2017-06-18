package org.mushare.httper;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.Serializable;

/**
 * Created by dklap on 4/28/2017.
 */

public class RequestSettingListBodyItem extends
        AbstractRequestSettingListItem<RequestSettingListBodyItem, RequestSettingListBodyItem
                .MyViewHolder> implements Serializable {
    RequestSettingListBodyItem() {
        super(RequestSettingType.body);
    }

//    public RequestSettingType getRequestSettingType() {
//        return RequestSettingType.body;
//    }

    @Override
    public int getType() {
        return R.id.request_setting_list_body;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.http_setting_body_item;
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(final View view) {
            super(view);
        }
    }
}
