package com.sudchiamanord.schoolmgr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.operations.DownloadOps;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericActivity;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.io.File;
import java.util.Properties;

/**
 * Created by rita on 12/8/15.
 */
public class DownloadActivity extends GenericActivity<DownloadOps>
{
    private static final String TAG = DownloadActivity.class.getSimpleName();

    private RingProgressDialog mOpProgressDialog;

    private String mUser;
    private String mPassword;
//    private String mSessionKey;
    private String mServerAddress;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_download);

        mOpProgressDialog = new RingProgressDialog (DownloadActivity.this);

        super.onCreate (savedInstanceState, DownloadOps.class);


        String currentUser = getIntent().getStringExtra (Tags.CURRENT_USER);
        if (currentUser == null) {
            Utils.generateErrorDialog (R.string.noCurrentUserError, this);
            finish();
            return;
        }

        // THIS PROPERTY READING WAS IN onStart()
        Properties properties = Utils.getProperties (getFileStreamPath (ConfigActivity.CONFIG_FILE),
                R.string.confFileReadError, this);

        String currentUserProperty = null;
        for (String property : properties.stringPropertyNames()) {
            if (currentUser.equals (properties.getProperty (property))) {
                currentUserProperty = property;
                break;
            }
        }
        if (currentUserProperty == null) {
            Utils.generateErrorDialog (R.string.userNotLoggedInError, this);
            finish();
            return;
        }
        mUser = properties.getProperty (currentUserProperty);

        String uuid = currentUserProperty.replace (Tags.USER, "");
        String currentPwProperty = Tags.PW + uuid;
        if (properties.getProperty (currentPwProperty) == null) {
            Utils.generateErrorDialog (R.string.userNotLoggedInError, this);
            finish();
            return;
        }

        mPassword = properties.getProperty (currentPwProperty);

        mServerAddress = properties.getProperty (Tags.SERVER_ADDRESS);
        if (mServerAddress == null) {
            Utils.generateErrorDialog (R.string.noDownloadServerAddressError, this);
            finish();
            return;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        new AlertDialog.Builder (DownloadActivity.this)
                .setTitle(R.string.generalWarning)
                .setMessage (R.string.startDownloadAlert)
                .setPositiveButton (android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                        getOps().startDownload (mUser, mPassword, mServerAddress);
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

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void notifySuccessfulDownload (String schoolYear, int nInfo)
    {
        String message = getString (R.string.downloadResults) + nInfo;
        Toast.makeText (this, message, Toast.LENGTH_SHORT).show();

        Properties properties = Utils.getProperties (this.getFileStreamPath (ConfigActivity.CONFIG_FILE),
                R.string.confFileReadError, this);
        properties.put (Tags.SCHOOL_YEAR, schoolYear);
        Utils.updateConfig (new File (ConfigActivity.CONFIG_FILE), properties, this);

        finish();
    }

    public void notifyFailedDownload (int message)
    {
        new AlertDialog.Builder (DownloadActivity.this)
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
