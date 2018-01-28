package com.sudchiamanord.schoolmgr.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.operations.BackupOps;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.Tags;

/**
 * Created by rita on 1/4/16.
 */
public class BackupActivity extends Activity
{
    private static final String TAG = BackupActivity.class.getSimpleName();

    private RingProgressDialog mOpProgressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_backup);

        mOpProgressDialog = new RingProgressDialog (BackupActivity.this);

        String user = getIntent().getStringExtra (Tags.CURRENT_USER);
        new BackupOps (this, user);
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
}
