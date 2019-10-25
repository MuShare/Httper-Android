package org.mushare.httper.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import org.mushare.httper.MainFragment;
import org.mushare.httper.R;

/**
 * Created by dklap on 6/5/2017.
 */

public class RequestRawBodyDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MainFragment fragment = (MainFragment) getTargetFragment();
        View view = ScrollView.inflate(getContext(), R.layout.dialog_raw_request_body, null);
        final EditText editText = view.findViewById(R.id.editText);
        editText.setText(fragment.getBody());
        // Set focus on the EditText.
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        return new AlertDialog.Builder(getContext()).setView(view).setPositiveButton(R.string
                .dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fragment.setBody(editText.getText().toString());
            }
        }).setNegativeButton(R.string.dialog_cancel, null).create();
    }
}
