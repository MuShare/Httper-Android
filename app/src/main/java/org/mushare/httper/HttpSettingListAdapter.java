package org.mushare.httper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dklap on 4/28/2017.
 */

public class HttpSettingListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<HttpSettingListItem> mDataset;

    HttpSettingListAdapter(List<HttpSettingListItem> dataset) {
        mDataset = dataset;
    }

    private int findLastPosition(int type) {
        if (mDataset == null || mDataset.size() == 0) return -1;
        for (int i = mDataset.size() - 1; i >= 0; i--) {
            if (mDataset.get(i).getType() == type) return i;
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position).getType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HttpSettingListItem.TYPE_HEADER:
            case HttpSettingListItem.TYPE_PARAMETER:
                return new ViewHolderKeyValuePair(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.http_setting_item, parent, false), new TextWatcherKey()
                        , new TextWatcherValue());
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
                ((ViewHolderKeyValuePair) holder).textWatcherKey.updatePosition(position);
                ((ViewHolderKeyValuePair) holder).textWatcherValue.updatePosition(position);
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
                        int position = findLastPosition(HttpSettingListItem.TYPE_HEADER) + 1;
                        if (position == 0) position = holder.getAdapterPosition() + 1;
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
                        int position = findLastPosition(HttpSettingListItem.TYPE_PARAMETER) + 1;
                        if (position == 0) position = holder.getAdapterPosition() + 1;
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
        TextWatcherKey textWatcherKey;
        TextWatcherValue textWatcherValue;

        ViewHolderKeyValuePair(View v, TextWatcherKey textWatcherKey, TextWatcherValue
                textWatcherValue) {
            super(v);
            this.textWatcherKey = textWatcherKey;
            this.textWatcherValue = textWatcherValue;
            textViewKey = (TextView) v.findViewById(R.id.textViewKey);
            textViewKey.addTextChangedListener(textWatcherKey);
            textViewValue = (TextView) v.findViewById(R.id.textViewValue);
            textViewValue.addTextChangedListener(textWatcherValue);
            imageButtonRemove = (ImageButton) v.findViewById(R.id.imageButtonRemove);
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

    private class TextWatcherKey implements TextWatcher {
        private HttpSettingListItemPair pair;

        void updatePosition(int position) {
            pair = (HttpSettingListItemPair) mDataset.get(position);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            pair.setKey(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class TextWatcherValue implements TextWatcher {
        private HttpSettingListItemPair pair;

        void updatePosition(int position) {
            pair = (HttpSettingListItemPair) mDataset.get(position);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            pair.setValue(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
