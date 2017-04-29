package org.mushare.httper;

/**
 * Created by dklap on 4/28/2017.
 */

public class HttpSettingListItemPair extends HttpSettingListItem {
    private String key;
    private String value;

    public HttpSettingListItemPair(int type) {
        super(type);
    }

    public HttpSettingListItemPair(int type, String key, String value) {
        super(type);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
