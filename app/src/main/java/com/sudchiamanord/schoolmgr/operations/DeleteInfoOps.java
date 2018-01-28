package com.sudchiamanord.schoolmgr.operations;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.activities.DeleteKidInfoActivity;
import com.sudchiamanord.schoolmgr.provider.DBContract;

import java.lang.ref.WeakReference;

/**
 * Created by rita on 2/4/16.
 */
public class DeleteInfoOps
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<DeleteKidInfoActivity> mActivity;

    public DeleteInfoOps (Activity activity, int kidInfoId)
    {
        mActivity = new WeakReference<>((DeleteKidInfoActivity) activity);

        if (kidInfoId < 0) {
            return;
        }

        mActivity.get().notifyProgressUpdate (0, R.string.deleteInfoDialogTitle,
                R.string.deleteInfoDialogExpl);

        Integer[] params = new Integer[1];
        params[0] = kidInfoId;
        new DeleteInfoOpsAsyncTask().execute (params);
    }

    private void notifyFinish (boolean success)
    {
        mActivity.get().notifyProgressUpdate (100, R.string.deleteInfoDialogTitle,
                R.string.deleteInfoDialogExpl);

        if (success) {
            mActivity.get().notifySuccess();
        }
        else {
            mActivity.get().notifyFail();
        }
    }

    class DeleteInfoOpsAsyncTask extends AsyncTask<Integer, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground (Integer... params)
        {
            int kidId = params[0];

            String whereClause = DBContract.KidEntry._ID + " = ?";
            String[] whereArgs = new String[1];
            whereArgs[0] = String.valueOf (kidId);
            int nRows = mActivity.get().getContentResolver().delete (
                    DBContract.KidEntry.KID_CONTENT_URI, whereClause, whereArgs);

            if (nRows < 0) {
                Log.d (TAG, "Impossible to delete row from the kid info table");
                return false;
            }
            Log.d (TAG, "Deleted " + nRows + " rows from the kid info table");

            whereClause = DBContract.AdoptionEntry.COLUMN_KID_ID + " = ?";
            nRows = mActivity.get().getContentResolver().delete (
                    DBContract.AdoptionEntry.ADOPTION_CONTENT_URI, whereClause, whereArgs);

            if (nRows < 0) {
                Log.d (TAG, "Impossible to delete row from the adoption info table");
                return false;
            }
            Log.d (TAG, "Deleted " + nRows + " rows from the adoption info table");

            return true;
        }

        @Override
        public void onPostExecute (Boolean success)
        {
            super.onPostExecute (success);
            notifyFinish (success);
        }
    }
}
