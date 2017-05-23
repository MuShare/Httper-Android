package org.mushare.httper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by dklap on 4/28/2017.
 */

public class HttpSettingListItem extends AbstractSectionableItem<HttpSettingListItem
        .MyViewHolder, HttpSettingListTitle> implements Parcelable {
    public static final Parcelable.Creator<HttpSettingListItem> CREATOR = new
            Creator<HttpSettingListItem>() {


                @Override
                public HttpSettingListItem createFromParcel(Parcel source) {
                    return new HttpSettingListItem(HttpSettingListTitle.getInstance(source
                            .readInt()), source.readString(), source.readString());
                }

                @Override
                public HttpSettingListItem[] newArray(int size) {
                    return new HttpSettingListItem[size];
                }
            };
    private String key;
    private String value;

    HttpSettingListItem(HttpSettingListTitle header) {
        super(header);
    }


    HttpSettingListItem(HttpSettingListTitle header, String key, String value) {
        super(header);
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

    public boolean hasContent() {
        return key != null && !key.isEmpty() && value != null && !value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return header.hashCode() * 31 * 31 + key.hashCode() * 31 + value.hashCode();
    }

    @Override
    public int getLayoutRes() {
        switch (header.getType()) {
            case HttpSettingListTitle.TYPE_PARAMETER:
                return R.layout.http_setting_param;
            case HttpSettingListTitle.TYPE_HEADER:
            default:
                return R.layout.http_setting_header;
        }
    }

    @Override
    public MyViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
                                         ViewGroup parent) {
        return new MyViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter,
                getHeader().getType() == HttpSettingListTitle.TYPE_HEADER);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, final MyViewHolder holder, int
            position, List payloads) {
        holder.textViewKey.setText(key);
        holder.textViewValue.setText(value);
        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position < 0 || position >= adapter.getItemCount()) return;
                if (adapter.getSectionItems(getHeader()).size() == 1) {
                    holder.textViewKey.setText(null);
                    holder.textViewValue.setText(null);
                    return;
                }
                holder.textViewKey.clearFocus();
                holder.textViewValue.clearFocus();
                adapter.removeItem(position);
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(header.getType());
        dest.writeString(key);
        dest.writeString(value);
    }

    static class MyViewHolder extends FlexibleViewHolder {
        TextView textViewKey;
        TextView textViewValue;
        ImageButton imageButtonRemove;

        MyViewHolder(View view, final FlexibleAdapter adapter, boolean isHttpHeader) {
            super(view, adapter);
            textViewKey = (TextView) view.findViewById(R.id.textViewKey);
            textViewKey.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ((HttpSettingListItem) adapter.getItem(getAdapterPosition())).setKey(s
                            .toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            if (isHttpHeader) {
                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(view.getContext(),
                        android.R.layout.simple_dropdown_item_1line, view.getContext()
                        .getResources().getStringArray(R.array.headers_array));
                ((AutoCompleteTextView) textViewKey).setAdapter(autoCompleteAdapter);
            }
            textViewValue = (TextView) view.findViewById(R.id.textViewValue);
            textViewValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ((HttpSettingListItem) adapter.getItem(getAdapterPosition())).setValue(s
                            .toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            imageButtonRemove = (ImageButton) view.findViewById(R.id.imageButtonRemove);
        }
    }
}
