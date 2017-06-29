package com.caloriesdiary.caloriesdiary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.LinearLayout;

public class CustomDialogFragment extends DialogFragment{

    LinearLayout dialogLayout;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        dialogLayout = (LinearLayout) getLayoutInflater()
//                .inflate(R.layout.dialog_layout, null);

        return builder
                .setTitle("Массу написал, сцук!")
                .setPositiveButton("OK", null)
                .setNegativeButton("Отмена", null)
               // .setView(R.layout.dialog_layout)
                .create();
    }
}
