package com.example.admin1.gymtracker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.admin1.gymtracker.R;

/**
        https://developer.android.com/reference/android/app/DialogFragment.html
        https://developer.android.com/reference/android/app/AlertDialog.html
        https://guides.codepath.com/android/Using-DialogFragment
        */
public class MessageDialog extends DialogFragment {

    public static MessageDialog newInstance(int title, int icon, String message){
        MessageDialog frgError = new MessageDialog();
        Bundle mBundle = new Bundle();
        mBundle.putInt("title", title);
        mBundle.putInt("icon", icon);
        mBundle.putString("message", message);
        frgError.setArguments(mBundle);
        return frgError;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title        = getArguments().getInt("title");
        int icon         = getArguments().getInt("icon");
        String message = getArguments().getString("message");

        return new AlertDialog.Builder(getActivity())
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface mDialog, int chosen) {
                                dismiss();
                            }
                        }
                )

                .create();
    }


}
