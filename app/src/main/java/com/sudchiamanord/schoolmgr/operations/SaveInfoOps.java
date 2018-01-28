package com.sudchiamanord.schoolmgr.operations;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.activities.KidInfoActivity;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.info.ListaAdozioni;
import com.sudchiamanord.schoolmgr.provider.DBContract.KidEntry;
import com.sudchiamanord.schoolmgr.provider.DBContract.AdoptionEntry;
import com.sudchiamanord.schoolmgr.provider.DBHelper;

import java.lang.ref.WeakReference;

/**
 * Created by rita on 12/28/15.
 */
public class SaveInfoOps
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<KidInfoActivity> mActivity;

    public SaveInfoOps (Activity activity, KidInfo kidInfo)
    {
        mActivity = new WeakReference<>((KidInfoActivity) activity);

        if (kidInfo == null) {
            return;
        }

        mActivity.get().notifyProgressUpdate (0, R.string.saveInfoDialogTitle,
                R.string.saveInfoDialogExpl);

        KidInfo[] params = new KidInfo[1];
        params[0] = kidInfo;
        new SaveInfoAsyncTask().execute(params);
    }

    private void notifyFinish (boolean success)
    {
        mActivity.get().notifyProgressUpdate (100, R.string.saveInfoDialogTitle,
                R.string.saveInfoDialogExpl);

        if (success) {
            mActivity.get().notifySuccess();
        }
        else {
            mActivity.get().notifyFail();
        }
    }

    class SaveInfoAsyncTask extends AsyncTask<KidInfo, Integer, Boolean>
    {
        @Override
        public Boolean doInBackground (KidInfo... param)
        {
            KidInfo kidInfo = param[0];

            ContentValues cv = DBHelper.getKidContentValues (kidInfo);
            if (kidInfo.getDbId() == null) {
                // inserting the new element in the db
                Uri uri = mActivity.get().getContentResolver().insert (KidEntry.KID_CONTENT_URI, cv);
                if (uri == null) {
                    return false;
                }

                long row = KidEntry.getRowFromURI (uri);
                if (row <= 0) {
                    return false;
                }

                String whereClause = KidEntry._ID + " = ?";
                String[] whereArgs = new String[1];
                whereArgs[0] = String.valueOf (row);
                Cursor cursor = mActivity.get().getContentResolver().query (KidEntry.KID_CONTENT_URI,
                        null, whereClause, whereArgs, null);

                if (cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex (KidEntry._ID);
                    if (kidInfo.getListaAdozioni() != null) {
                        for (ListaAdozioni adoptionInfo : kidInfo.getListaAdozioni()) {
                            cv = DBHelper.getAdoptionInfoValues (adoptionInfo, cursor.getInt (idx));
                            uri = mActivity.get().getContentResolver().insert (
                                    AdoptionEntry.ADOPTION_CONTENT_URI, cv);
                            if (uri == null) {
                                cursor.close();
                                return false;
                            }
                        }
                    }
                }
                cursor.close();
            }
            else {
                // updating element in the db
                String whereClause = KidEntry._ID + " = ?";
                String[] whereArgs = new String[1];
                whereArgs[0] = String.valueOf (kidInfo.getDbId());
                int rows = mActivity.get().getContentResolver().update (KidEntry.KID_CONTENT_URI,
                        cv, whereClause, whereArgs);
                if (rows <= 0) {
                    return false;
                }

                if (kidInfo.getListaAdozioni() != null) {
                    for (ListaAdozioni adoptionInfo : kidInfo.getListaAdozioni()) {
                        cv = DBHelper.getAdoptionInfoValues (adoptionInfo, kidInfo.getDbId());

                        whereClause = AdoptionEntry.COLUMN_SCHOOL_YEAR + " = ? AND " + AdoptionEntry.COLUMN_KID_ID + " = ? ";
                        whereArgs = new String[2];
                        whereArgs[0] = adoptionInfo.getAnnos();
                        whereArgs[1] = String.valueOf (kidInfo.getDbId());
                        Cursor cursor = mActivity.get().getContentResolver().query (
                                AdoptionEntry.ADOPTION_CONTENT_URI, null, whereClause, whereArgs,
                                null);

                        if (!cursor.moveToFirst()) {
                            // The adoption is not in the db yet
                            Uri uri = mActivity.get().getContentResolver().insert (
                                    AdoptionEntry.ADOPTION_CONTENT_URI, cv);
                            cursor.close();
                            if (uri == null) {
                                return false;
                            }
                        }
                        else {
                            // The adoption is in the db already
                            rows = mActivity.get().getContentResolver().update (
                                    AdoptionEntry.ADOPTION_CONTENT_URI, cv, whereClause, whereArgs);
                            cursor.close();
                            if (rows <= 0) {
                                return false;
                            }
                        }
                    }
                }
            }

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
