package org.mushare.httper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import org.mushare.httper.utils.RequestSettingDataUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dklap on 4/28/2017.
 */

public class RequestSettingListKVItem extends AbstractItem<RequestSettingListKVItem,
        RequestSettingListKVItem.MyViewHolder> implements Serializable {
    private String key;
    private String value;
    private RequestSettingType requestSettingType;

    private transient TextWatcher keyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            key = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private transient TextWatcher valueWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            value = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    RequestSettingListKVItem(RequestSettingType requestSettingType) {
        this.requestSettingType = requestSettingType;
    }


    RequestSettingListKVItem(RequestSettingType requestSettingType, String key, String value) {
        this.requestSettingType = requestSettingType;
        this.key = key;
        this.value = value;
    }

    public RequestSettingType getRequestSettingType() {
        return requestSettingType;
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

    @Override
    public int getType() {
        switch (requestSettingType) {
            case header:
                return R.id.request_setting_list_header;
            case parameter:
            default:
                return R.id.request_setting_list_param;
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.http_setting_kv_item;
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view, requestSettingType == RequestSettingType.header);
    }

    @Override
    public void bindView(MyViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.textViewKey.setText(key);
        holder.textViewValue.setText(value);
        holder.textViewKey.addTextChangedListener(keyWatcher);
        holder.textViewValue.addTextChangedListener(valueWatcher);
    }

    @Override
    public void unbindView(MyViewHolder holder) {
        super.unbindView(holder);
        holder.textViewKey.removeTextChangedListener(keyWatcher);
        holder.textViewValue.removeTextChangedListener(valueWatcher);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView textViewKey;
        TextView textViewValue;
        ImageButton imageButtonRemove;

        MyViewHolder(final View view, boolean isHttpHeader) {
            super(view);
            textViewKey = (AutoCompleteTextView) view.findViewById(R.id.textViewKey);
            if (isHttpHeader) {
                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(view.getContext(),
                        android.R.layout.simple_dropdown_item_1line, view.getContext()
                        .getResources().getStringArray(R.array.headers_array));
                textViewKey.setAdapter(autoCompleteAdapter);
            }
            textViewValue = (TextView) view.findViewById(R.id.textViewValue);
            imageButtonRemove = (ImageButton) view.findViewById(R.id.imageButtonRemove);
        }
    }

    public static class RemoveEvent extends ClickEventHook<IItem> {

        @Override
        public void onClick(View v, int position, FastAdapter<IItem> fastAdapter, IItem item) {
            if (item instanceof RequestSettingListKVItem && fastAdapter instanceof
                    FastItemAdapter) {
                RequestSettingListKVItem kvItem = (RequestSettingListKVItem) item;
                FastItemAdapter<IItem> fastItemAdapter = (FastItemAdapter<IItem>) fastAdapter;
                if (position < 0 || position >= fastItemAdapter.getItemCount()) return;
                if (RequestSettingDataUtils.isUnique(fastItemAdapter.getAdapterItems(), kvItem
                        .getRequestSettingType())) {
                    kvItem.setKey(null);
                    kvItem.setValue(null);
                    fastItemAdapter.notifyAdapterItemChanged(position);
                    return;
                }
                v.getRootView().clearFocus();
                InputMethodManager keyboard = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
                fastItemAdapter.remove(position);
            }
        }

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RequestSettingListKVItem.MyViewHolder) {
                return ((MyViewHolder) viewHolder).imageButtonRemove;
            }
            return null;
        }
    }
}
