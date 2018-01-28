package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.operations.DeleteInfoOps;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.Tags;

/**
 * Created by rita on 2/4/16.
 */
public class DeleteKidInfoActivity extends Activity
{
    private static final String TAG = DeleteKidInfoActivity.class.getSimpleName();

    private RingProgressDialog mOpProgressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_delete);

        mOpProgressDialog = new RingProgressDialog (DeleteKidInfoActivity.this);

        int kidInfoId = getIntent().getIntExtra (Tags.KID_INFO_DB_ID, -1);

        new DeleteInfoOps (this, kidInfoId);
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void notifySuccess()
    {
        Toast.makeText (this, R.string.deleteSuccess, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void notifyFail()
    {
        new AlertDialog.Builder (DeleteKidInfoActivity.this)
                .setTitle (R.string.generalError)
                .setMessage (R.string.deleteFailed)
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
