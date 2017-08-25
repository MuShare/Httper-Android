package org.mushare.httper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.CustomEventHook;

import org.mushare.httper.utils.RequestSettingDataUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dklap on 4/28/2017.
 */

public class RequestSettingListKVItem extends
        AbstractRequestSettingListItem<RequestSettingListKVItem, RequestSettingListKVItem
                .MyViewHolder> {
    private String key;
    private String value;

    RequestSettingListKVItem(RequestSettingType requestSettingType) {
        super(requestSettingType);
    }


    RequestSettingListKVItem(RequestSettingType requestSettingType, String key, String value) {
        super(requestSettingType);
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
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView textViewKey;
        EditText textViewValue;
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
            textViewValue = (EditText) view.findViewById(R.id.textViewValue);
            imageButtonRemove = (ImageButton) view.findViewById(R.id.imageButtonRemove);
        }
    }

    public static class RemoveEvent extends ClickEventHook<AbstractRequestSettingListItem> {

        @Override
        public void onClick(View v, int position, FastAdapter<AbstractRequestSettingListItem>
                fastAdapter, AbstractRequestSettingListItem item) {
            RequestSettingListKVItem kvItem = (RequestSettingListKVItem) item;
            FastItemAdapter<AbstractRequestSettingListItem> fastItemAdapter =
                    (FastItemAdapter<AbstractRequestSettingListItem>) fastAdapter;
            if (position < 0 || position >= fastItemAdapter.getItemCount()) return;
            if (RequestSettingDataUtils.isUnique(fastItemAdapter.getAdapterItems(), kvItem
                    .getRequestSettingType())) {
                if ((kvItem.getKey() != null && kvItem.getKey().length() > 0) || (kvItem.getValue
                        () != null && kvItem.getValue().length() > 0)) {
                    kvItem.setKey(null);
                    kvItem.setValue(null);
                    fastItemAdapter.notifyAdapterItemChanged(position);
                }
                return;
            }
            v.getRootView().clearFocus();
            InputMethodManager keyboard = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
            fastItemAdapter.remove(position);
        }

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RequestSettingListKVItem.MyViewHolder) {
                return ((MyViewHolder) viewHolder).imageButtonRemove;
            }
            return null;
        }
    }

    public static class textChangeEvent extends CustomEventHook<AbstractRequestSettingListItem> {

        @Override
        public void attachEvent(View view, final RecyclerView.ViewHolder viewHolder) {
            if (view.getId() == R.id.textViewKey) {
                ((EditText) view).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        RequestSettingListKVItem item = ((RequestSettingListKVItem) getItem
                                (viewHolder));
                        if (item == null) return;
                        item.setKey(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else if (view.getId() == R.id.textViewValue) {
                ((EditText) view).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        RequestSettingListKVItem item = ((RequestSettingListKVItem) getItem
                                (viewHolder));
                        if (item == null) return;
                        item.setValue(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }

        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RequestSettingListKVItem.MyViewHolder) {
                return Arrays.<View>asList(((MyViewHolder) viewHolder).textViewKey, (
                        (MyViewHolder) viewHolder).textViewValue);
            }
            return null;
        }
    }
}
