package org.mushare.httper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by dklap on 5/4/2017.
 */
public class ResponsePrettyFragment extends AbstractSaveFileFragment {

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
                            .responseBody, 1024));
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
        menu.add(Menu.NONE, R.id.context_menu_copy_pretty, 0, R.string.context_menu_copy);
        menu.add(Menu.NONE, R.id.context_menu_save_pretty, 1, R.string.context_menu_save_pretty);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_copy_pretty) {
            try {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService
                        (Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("response_body", TextUtils.join("\n",
                        texts)));
                Toast.makeText(getContext(), R.string.toast_copy, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.copy_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.context_menu_save_pretty) {
            preSaveFile();
            return true;
        } else return super.onContextItemSelected(item);
    }

    @Override
    public void saveFile(OutputStream outputStream) throws IOException {
        if (outputStream != null) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter
                    (outputStream);
            outputStreamWriter.write(TextUtils.join("\n", texts));
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
    }

    @Override
    public String defaultFileName() {
        String fileName = Uri.parse(((ResponseActivity) getActivity()).url).getLastPathSegment();
        return (fileName == null ? "" : fileName) + ".txt";
    }

}
