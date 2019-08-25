package org.mushare.httper;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;

/**
 * Created by dklap on 6/18/2017.
 */

public abstract class AbstractRequestSettingListItem<Item extends IItem & IClickable, VH extends
        RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements Serializable {
    RequestSettingType requestSettingType;

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
