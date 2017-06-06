package org.mushare.httper.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.mushare.httper.MainFragment;
import org.mushare.httper.R;

/**
 * Created by dklap on 6/5/2017.
 */

public class ClearRequestDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MainFragment fragment = (MainFragment) getTargetFragment();
        return new AlertDialog.Builder(getContext()).setTitle(R.string.dialog_warn).setMessage(R
                .string.clear_request_warn).setPositiveButton(R.string.dialog_yes, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.clearAll();
                    }
                }).setNegativeButton(R.string.dialog_cancel, null).create();
    }
}
