package com.opt.mobipag.data;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import com.opt.mobipag.gui.RegisterActivity;
import com.opt.mobipag.gui.SettingsActivity;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();

        Boolean reg = false;
        String date;
        if (getArguments() != null) {
            reg = getArguments().getBoolean("Register");
            date = getArguments().getString("Date");
            assert date != null;
            if (date.contains("/")) {
                String[] details = date.split("/| ");
                if (reg)
                    return new DatePickerDialog(getActivity(), (RegisterActivity) getActivity(), Integer.parseInt(details[0]), Integer.parseInt(details[1]) - 1, Integer.parseInt(details[2]));
                return new DatePickerDialog(getActivity(), (SettingsActivity) getActivity(), Integer.parseInt(details[0]), Integer.parseInt(details[1]) - 1, Integer.parseInt(details[2]));
            }
        }

        // Create a new instance of DatePickerDialog and return it
        if (reg)
            return new DatePickerDialog(getActivity(), (RegisterActivity) getActivity(), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        return new DatePickerDialog(getActivity(), (SettingsActivity) getActivity(), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
    }
}
