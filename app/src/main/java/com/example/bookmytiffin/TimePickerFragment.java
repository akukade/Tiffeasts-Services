package com.example.bookmytiffin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String curTime = String.format("%02d:%02d", hour, minute);
        //return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(),hour, minute, DateFormat.is24HourFormat(getActivity()));

        TimePickerDialog dialog = new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(),hour, minute, DateFormat.is24HourFormat(getActivity()));
        //dialog.getDatePicker().setTag(getTag());
        return dialog;

    }
}
