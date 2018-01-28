package com.sudchiamanord.schoolmgr.operations;

import android.app.Activity;
import android.util.Log;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.activities.SearchResultActivity;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.provider.DBContract.KidEntry;
import com.sudchiamanord.schoolmgr.provider.query.Query;
import com.sudchiamanord.schoolmgr.provider.query.QueryType;
import com.sudchiamanord.schoolmgr.util.scheleton.ConfigurableOps;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericAsyncTask;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericAsyncTaskOps;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rita on 12/21/15.
 */
public class SearchOps implements ConfigurableOps, GenericAsyncTaskOps<Object, Integer, List<KidInfo>>
{
    private static final String TAG = SearchOps.class.getSimpleName();

    private WeakReference<SearchResultActivity> mActivity;
    private GenericAsyncTask<Object, Integer, List<KidInfo>, SearchOps> mAsyncTask;
    private List<KidInfo> mResult;

    /**
     * Default constructor that's needed by the GenericActivity framework
     */
    public SearchOps()
    {
    }

    @Override
    public void onConfiguration (Activity activity, boolean firstTimeIn)
    {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d (TAG, "onConfiguration() called the " + time + " with activity = " + activity);

        mActivity = new WeakReference<>((SearchResultActivity) activity);

        if (firstTimeIn) {
            // Nothing to do for now
        }
        else {
            displayResults();
        }
    }

    public void query (QueryType queryType, Query kidInfoQuery, Query adoptionsQuery)
    {
        if (mAsyncTask != null) {
            mAsyncTask.cancel (true);
        }

        Object[] params = null;
        switch (queryType) {
            case all:
                params = new Object[1];
                params[0] = queryType;
                break;

            case kid_name:
                params = new Object[2];
                params[0] = queryType;
                params[1] = kidInfoQuery;
                break;

            case adoption_id:
                params = new Object[3];
                params[0] = queryType;
                params[1] = kidInfoQuery;
                params[2] = adoptionsQuery;
                break;

            case adopter:
                params = new Object[3];
                params[0] = queryType;
                params[1] = kidInfoQuery;
                params[2] = adoptionsQuery;
                break;
        }

        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute (params);
    }

    private void displayResults()
    {
        if (mResult != null) {
            publishProgress (RingProgressDialog.OPERATION_COMPLETED);
            mActivity.get().displayResults (mResult, null);
        }
    }

    @Override
    public void publishProgress (int progress)
    {
        mActivity.get().notifyProgressUpdate (progress, R.string.searchDialogTitle,
                R.string.searchDialogExpl);
    }

    @Override
    public List<KidInfo> doInBackground (Object... param)
    {
        QueryType queryType = (QueryType) param[0];
        Query kidInfoQuery;
        Query adoptionQuery;
        switch (queryType) {
            case all:
                return Utils.getKidInfo (mActivity.get(), new Query (KidEntry.KID_CONTENT_URI,
                        null, null, null, null));

            case kid_name:
                kidInfoQuery = (Query) param[1];
                return Utils.getKidInfo (mActivity.get(), kidInfoQuery);

            case adoption_id:
            case adopter:
                kidInfoQuery = (Query) param[1];
                adoptionQuery = (Query) param[2];
                List<Integer> kidsIdsList = Utils.getKidIds (mActivity.get(), adoptionQuery);
                List<KidInfo> kidsInfoList = new ArrayList<>();
                for (int id : kidsIdsList) {
                    String[] whereArgs = new String[1];
                    whereArgs[0] = String.valueOf (id);
                    Query completeKidInfoQuery = new Query (kidInfoQuery.uri, kidInfoQuery.projection,
                            kidInfoQuery.whereClause, whereArgs, kidInfoQuery.sortOrder);
                    kidsInfoList.addAll (Utils.getKidInfo (mActivity.get(), completeKidInfoQuery));
                }
                return kidsInfoList;
        }

        return null;

//        Uri uri = KidEntry.KID_CONTENT_URI;
//        String[] projection = null;
//        String whereClause = null;
//        String[] whereArgs = null;
//        String sortOrder = null;

//        if (param != null) {
//            if (param.length >= 1) {
//                whereClause = param[0];
//            }
//            if (param.length > 1) {
//                whereArgs = new String[param.length-1];
//                for (int i=1; i<param.length; i++) {
//                    whereArgs[i-1] = param[i];
//                }
//            }
//        }

//        Cursor cursor = mActivity.get().getContentResolver().query (uri, projection, whereClause,
//                whereArgs, sortOrder);
//
//        //Log.d (TAG, "Found " + cursor.getCount() + " kids in the db");
//
//        List<KidInfo> kidsInfo = new ArrayList<>();
//
//        int count = 0;
//        while (cursor.moveToNext()) {
//            KidInfo kidInfo = new KidInfo();
//            //Log.d (TAG, "Kid number " + (++count));
//            kidInfo.setDbId (cursor.getInt (cursor.getColumnIndex (KidEntry._ID)));
//            kidInfo.setIdado (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_SERVER_ID)));
//            kidInfo.setStato (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_STATUS)));
//            kidInfo.setAnome (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_KID_NAME)));
//            kidInfo.setAcogn (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_KID_LASTNAME)));
//            kidInfo.setAnick (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_KID_NICKNAME)));
//            kidInfo.setAdsex (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_SEX)));
//            kidInfo.setDatan (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_BDAY)));
//            kidInfo.setIndir (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_ADDRESS)));
//            kidInfo.setCitta (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_CITY)));
//            kidInfo.setAdtel (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_PHONE)));
//            int alive = cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_DAD_ALIVE));
//            kidInfo.setDadAlive (alive == 1);   // 0 (false) and 1 (true)
//            kidInfo.setPnome (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_NAME)));
//            kidInfo.setPcogn (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_LASTNAME)));
//            kidInfo.setPnick (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_NICKNAME)));
//            kidInfo.setPajob (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_JOB)));
//            kidInfo.setMomAlive (alive == 1);   // 0 (false) and 1 (true)
//            kidInfo.setMnome (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_NAME)));
//            kidInfo.setMcogn (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_LASTNAME)));
//            kidInfo.setMnick (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_NICKNAME)));
//            kidInfo.setMajob (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_JOB)));
//            kidInfo.setFfoto (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_PHOTO_NAME)));
//            kidInfo.setAnote (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_NOTES)));
//            kidInfo.setCreil (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_CREATE_DATE)));
//            kidInfo.setCreda (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_CREATE_BY)));
//            kidInfo.setScuol (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_SCHOOL_ID)));
//            kidInfo.setAnnos (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_SCHOOL_YEAR)));
//            kidInfo.setSclas (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_CLASS)));
//            kidInfo.setLastt (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_LAST_UPDATE_DATE)));
//            kidInfo.setLastu (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_LAST_UPDATE_USER)));
//            kidInfo.setLocalPhotoFile (cursor.getString (cursor.getColumnIndex (
//                    KidEntry.COLUMN_LOCAL_PHOTO_FILE)));
//
//            if (kidInfo.getLocalPhotoFile() != null) {
//                File filePath = mActivity.get().getFileStreamPath (kidInfo.getLocalPhotoFile());
//                kidInfo.setPhoto (decodeBitmap (filePath, 90, 90));
//            }
//
//            int idx = cursor.getColumnIndex (KidEntry._ID);
//            uri = AdoptionEntry.ADOPTION_CONTENT_URI;
//            whereClause = AdoptionEntry.COLUMN_KID_ID + " = ?";
//            whereArgs = new String[1];
//            whereArgs[0] = String.valueOf (cursor.getInt (idx));
//
//            Cursor adoptCursor = mActivity.get().getContentResolver().query (uri, null, whereClause,
//                    whereArgs, null);
//
//            //Log.d (TAG, "Found " + adoptCursor.getCount() + " adoption for the kid in the db");
//
//            List<ListaAdozioni> adoptionList = new ArrayList<>();
//            while (adoptCursor.moveToNext()) {
//                ListaAdozioni adoption = new ListaAdozioni();
//
//                adoption.setIdpag (adoptCursor.getInt (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_PAYMENT_CODE)));
//                adoption.setPclas (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_CLASS)));
//                adoption.setCodic (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTION_CODE)));
//                adoption.setIdscu (adoptCursor.getInt (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_SCHOOL_ID)));
//                adoption.setScuno (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_SCHOOL_NAME)));
//                adoption.setScuci (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_SCHOOL_CITY)));
//                adoption.setScupr (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTION_PREFIX)));
//                adoption.setAddid (adoptCursor.getInt (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTER_ID)));
//                adoption.setAdnom (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTER_NAME)));
//                adoption.setAdcog (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTER_LASTNAME)));
//                adoption.setAnnos (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_SCHOOL_YEAR)));
//
//                adoptionList.add (adoption);
//            }
//
//            adoptCursor.close();
//
//            kidInfo.setListaAdozioni (adoptionList);
//
//            kidsInfo.add (kidInfo);
//        }
//        cursor.close();
//
//        return kidsInfo;

    }

    @Override
    public void onPostExecute (List<KidInfo> kidsInfo, Object... param)
    {
        mResult = kidsInfo;
        publishProgress (RingProgressDialog.OPERATION_COMPLETED);
        Log.i (TAG, "Finished search execution");
        displayResults();
    }
}
