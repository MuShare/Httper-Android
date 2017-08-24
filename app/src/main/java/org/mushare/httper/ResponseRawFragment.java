package org.mushare.httper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.mushare.httper.utils.StringUtils;

import java.util.List;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResponseRawFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response_listview, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        if (((ResponseActivity) getActivity()).responseBody != null) {
            List<String> texts = StringUtils.splitLines(((ResponseActivity) getActivity())
                    .responseBody, 15);
//            String[] texts = ((ResponseActivity) getActivity()).responseBody.split("\n");
            listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_response_textview,
                    texts));
            registerForContextMenu(listView);
        }
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 2, 0, R.string.context_menu_copy);
        menu.add(Menu.NONE, 3, 1, R.string.context_menu_save);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 2) {
            try {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService
                        (Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("response_body", ((ResponseActivity)
                        getActivity()).responseBody));
                Toast.makeText(getContext(), R.string.toast_copy, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "too large", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == 3) {
            return true;
        } else return super.onContextItemSelected(item);
    }
}
