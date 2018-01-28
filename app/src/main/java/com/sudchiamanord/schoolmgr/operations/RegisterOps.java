package com.sudchiamanord.schoolmgr.operations;

import android.app.Activity;
import android.util.Log;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.activities.RegisterUserActivity;
import com.sudchiamanord.schoolmgr.operations.mediator.Proxy;
import com.sudchiamanord.schoolmgr.operations.results.RegisterResult;
import com.sudchiamanord.schoolmgr.util.scheleton.ConfigurableOps;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericAsyncTask;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericAsyncTaskOps;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;

import java.lang.ref.WeakReference;

/**
 * Created by rita on 12/12/15.
 */
public class RegisterOps implements ConfigurableOps, GenericAsyncTaskOps<String, Integer, RegisterResult>
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<RegisterUserActivity> mActivity;

    private RegisterResult mRegisterResult = null;

    private GenericAsyncTask<String, Integer, RegisterResult, RegisterOps> mAsyncTask;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public RegisterOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((RegisterUserActivity) activity);

        if (firstTimeIn) {
            // Nothing to do for now
        }
        else {
            updateResultsDisplay();
        }
    }

    private void updateResultsDisplay()
    {
        if (mRegisterResult != null) {
            publishProgress (RingProgressDialog.OPERATION_COMPLETED);
            if (mRegisterResult.isSuccessful()) {
                mActivity.get().notifySuccessfulRegistration (mRegisterResult.getUser(),
                        mRegisterResult.getPw(), mRegisterResult.getSchoolYear());
            }
            else {
                mActivity.get().notifyFailedRegistration (mRegisterResult.getUser(),
                        mRegisterResult.getMessage());
            }
        }
    }

    public void register (String user, String password, String serverURL)
    {
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        String[] params = new String[3];
        params[0] = user;
        params[1] = password;
        params[2] = serverURL;
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute (params);
    }

    @Override
    public void publishProgress (int progress)
    {
        mActivity.get().notifyProgressUpdate (progress, R.string.registerUserDialogTitle,
                R.string.registerUserDialogExpl);
    }

    @Override
    public RegisterResult doInBackground (String... param)
    {
        Log.i (TAG, "Started register to server doInBackground");
        return Proxy.doRegisterUser (param[0], param[1], param[2]);
    }

    @Override
    public void onPostExecute (RegisterResult registerResult, String... param)
    {
        mRegisterResult = registerResult;
        publishProgress (RingProgressDialog.OPERATION_COMPLETED);
        Log.i (TAG, "Finished register user execution");
        updateResultsDisplay();
    }
}
