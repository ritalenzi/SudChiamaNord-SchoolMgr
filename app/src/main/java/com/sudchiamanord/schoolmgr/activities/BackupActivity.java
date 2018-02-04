package com.sudchiamanord.schoolmgr.activities;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.operations.BackupOps;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.fragments.DatePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class to create a backup of data
 * @author Rita Lenzi (ritamichele.lenzi@gmail.com) - 1/4/16
 */
public class BackupActivity extends FragmentActivity
{
    private static final String TAG = BackupActivity.class.getSimpleName();

    private RingProgressDialog mOpProgressDialog;
    private Type mBackupType = Type.all;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_backup);
    }

    public void onRadioButtonClicked (View view)
    {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId())
        {
            case R.id.rbBackupAllData:
                if (checked) {
                    Toast.makeText (this, "Backup all data", Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.rbBackupToday:
                if (checked) {
                    Toast.makeText (this, "Backup today's data", Toast.LENGTH_SHORT).show();
                    break;
                }

            case R.id.rbBackupSince:
                if (checked) {
                    showDatePicker();
                    break;
                }
        }
    }

    public void showDatePicker()
    {
        DatePickerFragment date = new DatePickerFragment();
        final Calendar c = Calendar.getInstance();
        int year = c.get (Calendar.YEAR);
        int month = c.get (Calendar.MONTH);
        int day = c.get (Calendar.DAY_OF_MONTH);

        Bundle args = new Bundle();
        args.putInt ("year", year);
        args.putInt ("month", month);
        args.putInt ("day", day);
        date.setArguments (args);

        date.setCallBack (callback);
        date.show (getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Calendar date = Calendar.getInstance();
            date.set (Calendar.YEAR, year);
            date.set (Calendar.MONTH, monthOfYear);
            date.set (Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
            ((EditText) findViewById (R.id.txtBackupSince)).setText (dateFormat.format (date.getTime()));
        }
    };

    public void backup (View view)
    {
        mOpProgressDialog = new RingProgressDialog (BackupActivity.this);

        String user = getIntent().getStringExtra (Tags.CURRENT_USER);

        switch (mBackupType) {
            case all:
                new BackupOps (this, user, null);
                return;

            case today:
                Calendar date = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                new BackupOps (this, user, dateFormat.format (date.getTime()));
                return;

            case since:
                new BackupOps (this, user,
                        ((EditText) findViewById (R.id.txtBackupSince)).getText().toString());
                return;
        }

    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void notifySuccessfulBackup (String filename)
    {
        String message = getString (R.string.backupFile) + filename;
        new AlertDialog.Builder (BackupActivity.this)
                .setTitle (R.string.generalInfo)
                .setMessage (message)
                .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_info)
                .show();
    }

    public void notifyFailedBackup (int message)
    {
        new AlertDialog.Builder (BackupActivity.this)
                .setTitle (R.string.generalError)
                .setMessage (message)
                .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_alert)
                .show();
    }

    enum Type
    {
        all,
        today,
        since
    }
}
