package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.operations.LoadOps;
import com.sudchiamanord.schoolmgr.util.BarProgressDialog;
import com.sudchiamanord.schoolmgr.util.Tags;

/**
 * Created by rita on 1/17/16.
 */
public class LoadActivity extends Activity
{
    private static final String TAG = LoadActivity.class.getSimpleName();

    private BarProgressDialog mOpProgressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_load);

        mOpProgressDialog = new BarProgressDialog (LoadActivity.this);

        new AlertDialog.Builder (LoadActivity.this)
                .setTitle (R.string.generalWarning)
                .setMessage (R.string.startLoadAlert)
                .setPositiveButton (android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which) {
                        Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
                        intent.setType ("application/zip");
                        intent.addCategory (Intent.CATEGORY_OPENABLE);

                        try {
                            startActivityForResult (Intent.createChooser (intent,
                                            getString (R.string.selectFileMessage)),
                                    Tags.FILE_SELECT_REQUEST);
                        }
                        catch (android.content.ActivityNotFoundException ex) {
                            showDialog (R.string.generalError, getString (R.string.noFileManagerError),
                                    android.R.drawable.ic_dialog_alert);
                        }
                    }
                })
                .setNegativeButton (android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case Tags.FILE_SELECT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        showDialog (R.string.generalError, getString (R.string.loadFailed),
                                android.R.drawable.ic_dialog_alert);
                        return;
                    }

                    new LoadOps (this, uri);

//                    String filename = null;
//
//                    Cursor cursor;
//                    try {
//                        cursor = getContentResolver().query (uri, null, null, null, null);
//                    }
//                    catch (Exception e) {
//                        showDialog (R.string.generalError, getString (R.string.loadFailed),
//                                android.R.drawable.ic_dialog_alert);
//                        return;
//                    }
//
//                    if (cursor != null && cursor.moveToFirst()) {
//                        filename = cursor.getString (cursor.getColumnIndex (
//                                OpenableColumns.DISPLAY_NAME));
//                        cursor.close();
//                        Log.i (TAG, "Display Name: " + displayName);
//                    }

//                    new LoadOps (this, filename);
                }
                break;
        }
    }

    public void initProgressDialog (int max, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.initialize (max, dialogTitle, dialogExpl);
    }

    public void notifyProgressUpdate (int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (dialogExpl);
    }

    public void notifySuccessfulLoad (int nKidsInfo)
    {
        mOpProgressDialog.dismiss();
        String message = getString (R.string.loadedInfoMessage) + nKidsInfo;
        showDialog (R.string.generalInfo, message, android.R.drawable.ic_dialog_info);
    }

    public void notifyFailedLoad (int message)
    {
        mOpProgressDialog.dismiss();
        showDialog (R.string.generalError, getString (message), android.R.drawable.ic_dialog_alert);
    }

    private void showDialog (int title, String message, int iconId)
    {
        new AlertDialog.Builder (LoadActivity.this)
                .setTitle (title)
                .setMessage (message)
                .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                        finish();
                    }
                })
                .setIcon (iconId)
                .show();
    }
}
