package com.example.admin1.gymtracker.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.example.admin1.gymtracker.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/*
This picker Fragment was built with the help of the Google Documents
https://developer.android.com/guide/topics/ui/controls/pickers.html
 */

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter dtFmt = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
    private setDateText interfaceImplementor;
    private DateTime dtReceived;
    public DatePicker(){

    }

    public DatePicker(DateTime dtReceived){
        this.dtReceived = dtReceived;
    }

    public interface setDateText {
         void setDateText(String stMessage);
    }

    // I am supporting version 19 -25 so I must keep on Attach even though it is depreciated
    @Override
    public void onAttach(Activity mActivity) {
        super.onAttach(mActivity);
        this.interfaceImplementor = (setDateText) mActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int iYear ;
        int iMonth;
        int iDay;
        Bundle data = this.getArguments();
        if (!(data == null)){
            dtReceived = dtFmt.parseDateTime(data.getString("existingDate",null));
        }

        if (dtReceived == null){
            iYear  = c.get(Calendar.YEAR);
            iMonth = c.get(Calendar.MONTH);
            iDay   = c.get(Calendar.DAY_OF_MONTH);
        }
        else{
            iYear = dtReceived.getYear();
            iMonth = dtReceived.getMonthOfYear() - 1;
            iDay   = dtReceived.getDayOfMonth();
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.AppTheme, this, iYear, iMonth, iDay);
    }


    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        String stDate = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (month + 1)) + "/" + year;
        interfaceImplementor.setDateText(stDate);

    }
}
