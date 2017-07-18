package com.example.admin1.gymtracker.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;

import java.util.Calendar;

/*
This picker Fragment was built with the help of the Google Documents
https://developer.android.com/guide/topics/ui/controls/pickers.html
 */

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private String stExistingText;
    private DatePicker mDatePicker;
    public DatePicker(String stExistingText){
        this.stExistingText = stExistingText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int iYear  = c.get(Calendar.YEAR);
        int iMonth = c.get(Calendar.MONTH);
        int iDay   = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.AppTheme, this, iYear, iMonth, iDay);
    }


    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        TextView tvDate = (TextView) getActivity().findViewById(R.id.tvDate);
        tvDate.setText(stExistingText + String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month)+ "/" + year);

    }
}
