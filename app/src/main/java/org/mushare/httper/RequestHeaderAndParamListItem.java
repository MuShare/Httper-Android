package org.mushare.httper;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dklap on 4/28/2017.
 */

public class RequestHeaderAndParamListItem extends AbstractItem<RequestHeaderAndParamListItem,
        RequestHeaderAndParamListItem.MyViewHolder> implements Serializable {
    private String key;
    private String value;
    private Type title;

    private TextWatcher keyWatcher = new TextWatcher() {
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

    private TextWatcher valueWatcher = new TextWatcher() {
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

    RequestHeaderAndParamListItem(Type title) {
        this.title = title;
    }


    RequestHeaderAndParamListItem(Type title, String key, String value) {
        this.title = title;
        this.key = key;
        this.value = value;
    }

    public Type getTitle() {
        return title;
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
        return 0;
    }

    @Override
    public int getLayoutRes() {
        switch (title) {
            case parameter:
                return R.layout.http_setting_param;
            case header:
            default:
                return R.layout.http_setting_header;
        }
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view, title == Type.header);
    }

    @Override
    public void bindView(MyViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.textViewKey.setText(key);
        holder.textViewValue.setText(value);
        holder.textViewKey.addTextChangedListener(keyWatcher);
        holder.textViewValue.addTextChangedListener(valueWatcher);
//        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                if (position < 0 || position >= adapter.getItemCount()) return;
//                if (adapter.getSectionItems(getHeader()).size() == 1) {
//                    holder.textViewKey.setText(null);
//                    holder.textViewValue.setText(null);
//                    return;
//                }
//                if (holder.itemView.hasFocus()) {
//                    holder.textViewKey.clearFocus();
//                    holder.textViewValue.clearFocus();
//                    InputMethodManager keyboard = (InputMethodManager) holder.itemView
//                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    keyboard.hideSoftInputFromWindow(holder.itemView.getWindowToken(), 0);
//                }
//                adapter.removeItem(position);
//            }
//        });
    }

    @Override
    public void unbindView(MyViewHolder holder) {
        super.unbindView(holder);
        holder.textViewKey.removeTextChangedListener(keyWatcher);
        holder.textViewValue.removeTextChangedListener(valueWatcher);
    }

    enum Type {
        header, parameter
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKey;
        TextView textViewValue;
        ImageButton imageButtonRemove;

        MyViewHolder(View view, boolean isHttpHeader) {
            super(view);
            textViewKey = (TextView) view.findViewById(R.id.textViewKey);
            if (isHttpHeader) {
                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(view.getContext(),
                        android.R.layout.simple_dropdown_item_1line, view.getContext()
                        .getResources().getStringArray(R.array.headers_array));
                ((AutoCompleteTextView) textViewKey).setAdapter(autoCompleteAdapter);
            }
            textViewValue = (TextView) view.findViewById(R.id.textViewValue);
            imageButtonRemove = (ImageButton) view.findViewById(R.id.imageButtonRemove);
        }
    }
}
