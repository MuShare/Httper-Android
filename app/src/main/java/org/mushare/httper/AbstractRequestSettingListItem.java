package org.mushare.httper;

import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by dklap on 6/18/2017.
 */

public abstract class AbstractRequestSettingListItem<Item extends IItem & IClickable, VH extends
        RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {
    protected RequestSettingType requestSettingType;

    AbstractRequestSettingListItem(RequestSettingType requestSettingType) {
        this.requestSettingType = requestSettingType;
    }

    public RequestSettingType getRequestSettingType() {
        return requestSettingType;
    }

    public enum RequestSettingType {
        header, parameter, body
    }
}
