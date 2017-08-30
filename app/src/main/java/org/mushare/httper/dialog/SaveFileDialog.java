package org.mushare.httper.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mushare.httper.AbstractSaveFileFragment;
import org.mushare.httper.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dklap on 6/5/2017.
 */

public class SaveFileDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AbstractSaveFileFragment fragment = (AbstractSaveFileFragment) getTargetFragment();
        View view = LinearLayout.inflate(getContext(), R.layout.dialog_save_file, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(getResources().getString(R.string.save_file_name, Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()));
        final EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern p = Pattern.compile("[\\\\/:*?\"<>|\\x00-\\x1f\\x7f]");   // the pattern
                // to search for
                Matcher m = p.matcher(s);
                if (m.find()) {
                    String newString = s.toString().replaceAll
                            ("[\\\\/:*?\"<>|\\x00-\\x1f\\x7f]+", "");
                    s.clear();
                    s.append(newString);
                }
            }
        });
        editText.setText(fragment.defaultFileName());
        return new AlertDialog.Builder(getContext()).setTitle(R.string.dialog_save_file_title)
                .setView(view).setPositiveButton(R.string
                        .dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OutputStream outputStream = null;
                        try {
                            String fileName = editText.getText().toString();
                            fileName = fileName.isEmpty() ? fragment.defaultFileName() : fileName;
                            File file = new File(Environment.getExternalStoragePublicDirectory
                                    (Environment.DIRECTORY_DOWNLOADS).getPath(), fileName);
                            Environment.getExternalStoragePublicDirectory(Environment
                                    .DIRECTORY_DOWNLOADS).mkdirs();
                            outputStream = new FileOutputStream(file);
                            fragment.saveFile(outputStream);
                            Toast.makeText(getContext(), getString(R.string.save_file_success,
                                    file.getPath()), Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getContext(), R.string.save_file_error, Toast
                                    .LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (outputStream != null) try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).setNegativeButton(R.string.dialog_cancel, null).create();
    }
}
