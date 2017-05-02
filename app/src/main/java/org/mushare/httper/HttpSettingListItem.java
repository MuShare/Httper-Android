package org.mushare.httper;

import java.io.Serializable;

/**
 * Created by dklap on 4/29/2017.
 */

public class HttpSettingListItem implements Serializable {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_HEADER_TITLE = 1;
    public static final int TYPE_PARAMETER = 2;
    public static final int TYPE_PARAMETER_TITLE = 3;
    private int type;

    public HttpSettingListItem(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public boolean hasContent() {
        return false;
    }
}
