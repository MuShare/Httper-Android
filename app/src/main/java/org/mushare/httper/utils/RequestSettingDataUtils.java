package org.mushare.httper.utils;

import com.mikepenz.fastadapter.IItem;

import org.mushare.httper.RequestSettingListKVItem;
import org.mushare.httper.RequestSettingListStickTitle;
import org.mushare.httper.RequestSettingType;

import java.util.List;

/**
 * Created by dklap on 6/4/2017.
 */

public class RequestSettingDataUtils {
    public static boolean isUnique(List<IItem> data, RequestSettingType type) {
        boolean flag = false;
        for (IItem iItem : data) {
            if (flag)
                return !(iItem instanceof RequestSettingListKVItem) || ((RequestSettingListKVItem)
                        iItem).getRequestSettingType() != type;
            if (iItem instanceof RequestSettingListKVItem && ((RequestSettingListKVItem) iItem)
                    .getRequestSettingType() == type) flag = true;
        }
        return flag;
    }

    public static int lastIndexOf(List<IItem> data, RequestSettingType type) {
        for (int i = data.size() - 1; i >= 0; i--) {
            IItem iItem = data.get(i);
            if (iItem instanceof RequestSettingListKVItem && ((RequestSettingListKVItem) iItem)
                    .getRequestSettingType() == type)
                return i;
        }
        return -1;
    }

    public static RequestSettingType findTitleTypeBeforeIndex(List<IItem> data, int index) {
        for (int i = index - 1; i >= 0; i--) {
            IItem iItem = data.get(i);
            if (iItem instanceof RequestSettingListStickTitle)
                return ((RequestSettingListStickTitle) iItem).getRequestSettingType();
        }
        return null;
    }
}
