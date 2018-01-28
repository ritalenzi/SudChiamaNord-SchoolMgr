package com.sudchiamanord.schoolmgr.operations;

import android.app.Activity;
import android.util.Log;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.activities.DownloadActivity;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.operations.mediator.Proxy;
import com.sudchiamanord.schoolmgr.operations.results.DownloadResult;
import com.sudchiamanord.schoolmgr.operations.results.ImageDownloadResult;
import com.sudchiamanord.schoolmgr.operations.results.RegisterResult;
import com.sudchiamanord.schoolmgr.util.scheleton.ConfigurableOps;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericAsyncTask;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericAsyncTaskOps;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.provider.DBContract.KidEntry;
import com.sudchiamanord.schoolmgr.provider.DBContract.AdoptionEntry;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by rita on 12/8/15.
 */
public class DownloadOps implements ConfigurableOps, GenericAsyncTaskOps<String, Integer, DownloadResult>
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<DownloadActivity> mActivity;
    private GenericAsyncTask<String, Integer, DownloadResult, DownloadOps> mAsyncTask;
    private DownloadResult mResult;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public DownloadOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((DownloadActivity) activity);

        if (firstTimeIn) {
            // Nothing to do for now
        }
        else {
            displayResults();
        }
    }

    private void displayResults()
    {
        if (mResult != null) {
            publishProgress (RingProgressDialog.OPERATION_COMPLETED);
            if (mResult.isSuccessful()) {
                int nInfo = 0;
                if (mResult.getKidsInfo() != null) {
                    nInfo = mResult.getKidsInfo().size();
                }
                mActivity.get().notifySuccessfulDownload (mResult.getSchoolYear(), nInfo);
            }
            else {
                mActivity.get().notifyFailedDownload (mResult.getMessage());
            }
        }
    }

    public void startDownload (String user, String password, String serverAddress)
    {
        if ((user == null) || (password == null) || (serverAddress == null)) {
            return;
        }

        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        String[] params = new String[3];
        params[0] = user;
        params[1] = password;
        params[2] = serverAddress;
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute (params);
    }

    @Override
    public void publishProgress (int progress) 
    {
        mActivity.get().notifyProgressUpdate (progress, R.string.downloadDialogTitle,
                R.string.downloadDialogExpl);
    }

    @Override
    public DownloadResult doInBackground (String... param)
    {
        String user = param[0];
        String password = param[1];
        String serverAddress = param[2];
        RegisterResult registerResult = Proxy.doRegisterUser (user, password, serverAddress);
        if ((registerResult == null) || (!registerResult.isSuccessful())) {
            return new DownloadResult (false, R.string.registerUserFailed, true, null, null);
        }

        if (registerResult.getSessionKey() == null) {
            return new DownloadResult (false, R.string.noDownloadSessionKeyError, true, null, null);
        }

        if (registerResult.getSchoolYear() == null) {
            return new DownloadResult (false, R.string.noDownloadSchoolYearError, true, null, null);
        }

        DownloadResult result = Proxy.downloadKidsInfo (registerResult.getSessionKey(), serverAddress,
                registerResult.getSchoolYear());
        if ((result == null) || (!result.isSuccessful())) {
            return null;
        }

        int rowDeleted = mActivity.get().getContentResolver().delete (KidEntry.KID_CONTENT_URI,
                null, null);
        Log.d (TAG, "Deleted " + rowDeleted + " rows from " + KidEntry.KID_TABLE);
        rowDeleted = mActivity.get().getContentResolver().delete (AdoptionEntry.ADOPTION_CONTENT_URI,
                null, null);
        Log.d (TAG, "Deleted " + rowDeleted + " rows from " + AdoptionEntry.ADOPTION_TABLE);
        int imagesDeleted = Utils.deleteAllImages (mActivity.get());
        Log.d (TAG, "Deleted " + imagesDeleted + " images");

        for (KidInfo kidInfo : result.getKidsInfo()) {
            kidInfo.setDadAlive (true);
            if (kidInfo.getPajob() != null) {
                if (kidInfo.getPajob().equalsIgnoreCase ("dead")) {
                    kidInfo.setDadAlive (false);
                }
                else {
                    kidInfo.setDadAlive (true);
                }
            }

            kidInfo.setMomAlive(true);
            if (kidInfo.getMajob() != null) {
                if (kidInfo.getMajob().equalsIgnoreCase ("dead")) {
                    kidInfo.setMomAlive (false);
                }
                else {
                    kidInfo.setMomAlive (true);
                }
            }


            boolean downloadPhoto = true;
            String photoURL = kidInfo.getFfoto();
            if (photoURL == null) {
                Log.d (TAG, "No photo url");
                downloadPhoto = false;
            }

            if ((photoURL != null) && (!photoURL.endsWith ("JPG")) && (!photoURL.endsWith ("JPEG")) &&
                    (!photoURL.endsWith ("jpg")) && (!photoURL.endsWith ("jpeg"))) {
                Log.d (TAG, "Wrong format of the photo");
                downloadPhoto = false;
            }

            if ((photoURL != null) && (!photoURL.startsWith ("http://")) &&
                    (!photoURL.startsWith ("https://"))) {
                photoURL = "http://" + photoURL;
            }

            if (downloadPhoto) {
                ImageDownloadResult imageDownloadResult = Proxy.downloadImage (photoURL, mActivity.get());

//                if (imageDownloadResult == null) {
//                    Log.d (TAG, "Image download failed for " + photoURL);
//                }
//                else if (!imageDownloadResult.isSuccessful()) {
//                    Log.d (TAG, "Image download failed for " + photoURL + ": " +
//                            mActivity.get().getString (imageDownloadResult.getMessage()));
//                }
//                else {
//                    kidInfo.setLocalPhotoFile (imageDownloadResult.getImageFilename());
//                }
                if ((imageDownloadResult != null) && (imageDownloadResult.isSuccessful())) {
                    kidInfo.setLocalPhotoFile (imageDownloadResult.getImageFilename());
                }
            }

            Utils.insert (mActivity.get(), kidInfo);
        }

        return result;
    }

    @Override
    public void onPostExecute (DownloadResult downloadResult, String... param)
    {
        mResult = downloadResult;
        publishProgress (RingProgressDialog.OPERATION_COMPLETED);
        Log.i (TAG, "Finished download execution");
        displayResults();
    }
}
