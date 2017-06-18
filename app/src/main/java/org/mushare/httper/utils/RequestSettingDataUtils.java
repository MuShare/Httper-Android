package org.mushare.httper.utils;

import org.mushare.httper.AbstractRequestSettingListItem;
import org.mushare.httper.AbstractRequestSettingListItem.RequestSettingType;
import org.mushare.httper.RequestSettingListKVItem;

import java.util.List;

/**
 * Created by dklap on 6/4/2017.
 */

public class RequestSettingDataUtils {
    public static boolean isUnique(List<AbstractRequestSettingListItem> data, RequestSettingType
            type) {
        boolean flag = false;
        for (AbstractRequestSettingListItem iItem : data) {
            if (flag)
                return !(iItem instanceof RequestSettingListKVItem) || iItem
                        .getRequestSettingType() != type;
            if (iItem instanceof RequestSettingListKVItem && iItem.getRequestSettingType() == type)
                flag = true;
        }
        return flag;
    }

    public static int lastIndexOf(List<AbstractRequestSettingListItem> data, RequestSettingType
            type) {
        for (int i = data.size() - 1; i >= 0; i--) {
            AbstractRequestSettingListItem iItem = data.get(i);
            if (iItem instanceof RequestSettingListKVItem && iItem.getRequestSettingType() == type)
                return i;
        }
        return -1;
    }

    public static RequestSettingType findTitleTypeBeforeIndex
            (List<AbstractRequestSettingListItem> data, int index) {
        if (index == 0) return RequestSettingType.header;
        return data.get(index - 1).getRequestSettingType();
    }
}
