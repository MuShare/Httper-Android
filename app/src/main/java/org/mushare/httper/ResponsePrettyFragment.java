package org.mushare.httper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.mushare.httper.utils.MyJSONArray;
import org.mushare.httper.utils.MyJSONObject;
import org.mushare.httper.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by dklap on 5/4/2017.
 */
public class ResponsePrettyFragment extends Fragment {
    ArrayList<CharSequence> texts;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response_listview, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        if (((ResponseActivity) getActivity()).responseBody != null) {
            try {
                MyJSONObject jsonObject = new MyJSONObject(((ResponseActivity) getActivity())
                        .responseBody);
                texts = jsonObject.getCharSequences(2);
            } catch (JSONException e) {
                try {
                    MyJSONArray jsonArray = new MyJSONArray(((ResponseActivity) getActivity())
                            .responseBody);
                    texts = jsonArray.getCharSequences(2);
                } catch (JSONException e1) {
                    texts = new ArrayList<>();
                    texts.addAll(StringUtils.splitLines(((ResponseActivity) getActivity())
                            .responseBody, 15));
                }
            }
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
        menu.add(Menu.NONE, 0, 0, R.string.context_menu_copy);
        menu.add(Menu.NONE, 1, 1, R.string.context_menu_save_pretty);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            try {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService
                        (Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("response_body", TextUtils.join("",
                        texts)));
                Toast.makeText(getContext(), R.string.toast_copy, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "too large", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == 1) {
            return true;
        } else return super.onContextItemSelected(item);
    }
}
