package com.sudchiamanord.schoolmgr.util.fragments;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by rita on 11/11/15.
 */
public class DatePickerFragment extends DialogFragment
{
    OnDateSetListener callback;
    private int year, month, day;

    public DatePickerFragment()
    {
    }

    public void setCallBack (OnDateSetListener callback)
    {
        this.callback = callback;
    }

    @Override
    public void setArguments (Bundle args)
    {
        super.setArguments (args);
        year = args.getInt ("year");
        month = args.getInt ("month");
        day = args.getInt ("day");
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        return new DatePickerDialog (getActivity(), callback, year, month, day);
    }
}
