package org.mushare.httper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dklap on 4/28/2017.
 */

public class HttpSettingListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static List<HttpSettingListItem> mDataset;

    HttpSettingListAdapter(List<HttpSettingListItem> dataset) {
        mDataset = dataset;
    }

    private int findInsertPosition(int type, int endPosition) {
//        if (mDataset == null || mDataset.size() == 0) return -1;
        for (int i = mDataset.size() - 1; i > endPosition; i--) {
            if (mDataset.get(i).getType() == type) return i + 1;
        }
        return endPosition + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position).getType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HttpSettingListItem.TYPE_HEADER:
                return new ViewHolderAutoCompleteKeyValuePair(LayoutInflater.from(parent
                        .getContext())
                        .inflate(R.layout.http_setting_header, parent, false));
            case HttpSettingListItem.TYPE_PARAMETER:
                return new ViewHolderKeyValuePair(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.http_setting_param, parent, false));
            case HttpSettingListItem.TYPE_HEADER_TITLE:
            case HttpSettingListItem.TYPE_PARAMETER_TITLE:
                return new ViewHolderTitle(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .http_setting_title, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HttpSettingListItem httpSettingListItem = mDataset.get(position);
        switch (httpSettingListItem.getType()) {
            case HttpSettingListItem.TYPE_HEADER:
            case HttpSettingListItem.TYPE_PARAMETER:
                ((ViewHolderKeyValuePair) holder).textViewKey.setText(((HttpSettingListItemPair)
                        httpSettingListItem).getKey());
                ((ViewHolderKeyValuePair) holder).textViewValue.setText((
                        (HttpSettingListItemPair) httpSettingListItem).getValue());
                ((ViewHolderKeyValuePair) holder).imageButtonRemove.setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        if (position < 0 || position >= mDataset.size()) return;
                        ((ViewHolderKeyValuePair) holder).textViewKey.clearFocus();
                        ((ViewHolderKeyValuePair) holder).textViewValue.clearFocus();
                        mDataset.remove(position);
                        notifyItemRemoved(position);
                    }
                });
                break;
            case HttpSettingListItem.TYPE_HEADER_TITLE:
                ((ViewHolderTitle) holder).textViewTitle.setText(R.string.main_view_headers);
                ((ViewHolderTitle) holder).imageButtonAdd.setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = findInsertPosition(HttpSettingListItem.TYPE_HEADER, holder
                                .getAdapterPosition());
                        mDataset.add(position, new HttpSettingListItemPair(HttpSettingListItem
                                .TYPE_HEADER));
                        notifyItemInserted(position);
                    }
                });
                break;
            case HttpSettingListItem.TYPE_PARAMETER_TITLE:
                ((ViewHolderTitle) holder).textViewTitle.setText(R.string.main_view_params);
                ((ViewHolderTitle) holder).imageButtonAdd.setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = findInsertPosition(HttpSettingListItem.TYPE_PARAMETER,
                                holder.getAdapterPosition());
                        mDataset.add(position, new HttpSettingListItemPair(HttpSettingListItem
                                .TYPE_PARAMETER));
                        notifyItemInserted(position);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static class ViewHolderKeyValuePair extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textViewKey;
        TextView textViewValue;
        ImageButton imageButtonRemove;

        ViewHolderKeyValuePair(View v) {
            super(v);
            textViewKey = (TextView) v.findViewById(R.id.textViewKey);
            textViewKey.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ((HttpSettingListItemPair) mDataset.get(getAdapterPosition())).setKey(s
                            .toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            textViewKey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        InputMethodManager keyboard = (InputMethodManager) textViewKey.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(textViewKey.getWindowToken(), 0);
                    }
                }
            });
            textViewValue = (TextView) v.findViewById(R.id.textViewValue);
            textViewValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ((HttpSettingListItemPair) mDataset.get(getAdapterPosition())).setValue(s
                            .toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            textViewValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        InputMethodManager keyboard = (InputMethodManager) textViewValue
                                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(textViewValue.getWindowToken(), 0);
                    }
                }
            });
            imageButtonRemove = (ImageButton) v.findViewById(R.id.imageButtonRemove);
        }

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static class ViewHolderAutoCompleteKeyValuePair extends ViewHolderKeyValuePair {

        ViewHolderAutoCompleteKeyValuePair(View v) {
            super(v);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(textViewKey.getContext(), android.R
                    .layout.simple_dropdown_item_1line, textViewKey.getContext().getResources()
                    .getStringArray(R.array.headers_array));
            ((AutoCompleteTextView) textViewKey).setAdapter(adapter);
        }

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static class ViewHolderTitle extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textViewTitle;
        ImageButton imageButtonAdd;

        ViewHolderTitle(View v) {
            super(v);
            textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            imageButtonAdd = (ImageButton) v.findViewById(R.id.imageButtonAdd);
        }

    }
}
