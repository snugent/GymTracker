package com.example.admin1.gymtracker.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.admin1.gymtracker.R;

/**
 * Build with the help of
 https://developer.android.com/reference/android/app/DialogFragment.html
 https://developer.android.com/reference/android/app/AlertDialog.html
 https://guides.codepath.com/android/Using-DialogFragment
 */
public class ConfirmDialog extends DialogFragment {
    private setConfirm interfaceImplementor;
    public interface setConfirm {
        void setConfirm(boolean blChoice);
    }

    public static ConfirmDialog newInstance(int title, int icon, String message){
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle mBundle = new Bundle();
        mBundle.putInt("title", title);
        mBundle.putInt("icon", icon);
        mBundle.putString("message", message);
        confirmDialog.setArguments(mBundle);
        return confirmDialog;
    }
    // I am using version 19 -25 so I must keep on Attach even though it is depreciated
    @Override
    public void onAttach(Activity mActivity) {
        super.onAttach(mActivity);
        this.interfaceImplementor = (setConfirm) mActivity;
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
                .setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface mDialog, int chosen) {
                            interfaceImplementor.setConfirm(true);
                            dismiss();
                        }
                    }
                )
                .setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            interfaceImplementor.setConfirm(false);
                            dialog.cancel();
                        }
                    }
                )
                .create();
    }
}// End methoed
