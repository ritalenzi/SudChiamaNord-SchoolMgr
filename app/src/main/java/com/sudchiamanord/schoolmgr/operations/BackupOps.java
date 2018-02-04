package com.sudchiamanord.schoolmgr.operations;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.activities.BackupActivity;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.provider.DBContract;
import com.sudchiamanord.schoolmgr.provider.query.Query;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Container for the AsyncTask that does the backup of the data
 * @author Rita Lenzi (ritamichele.lenzi@gmail.com) - 1/4/16.
 */
public class BackupOps
{
    private final String TAG = getClass().getSimpleName();

    final static String jsonFile = "kidsInfo.json";

    private WeakReference<BackupActivity> mActivity;

    public BackupOps (Activity activity, String user, String since)
    {
        mActivity = new WeakReference<>((BackupActivity) activity);
        mActivity.get().notifyProgressUpdate (0, R.string.backupDialogTitle,
                R.string.backupDialogExpl);

        String[] params = new String[2];
        params[0] = user;
        params[1] = since;
        new BackupAsyncTask().execute (params);
    }

    class BackupAsyncTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground (String... params)
        {
            Uri uri = DBContract.KidEntry.KID_CONTENT_URI;
            String[] projection = null;
            String whereClause = null
            String[] whereArgs = null;
            String sortOrder = null;
            List<KidInfo> kidsInfo = Utils.getKidInfo (mActivity.get(), new Query (uri, projection,
                    whereClause, whereArgs, sortOrder));

            if (kidsInfo == null) {
                return null;
            }
            for (KidInfo kidInfo : kidsInfo) {
                kidInfo.setPhoto (null);
            }

//            Gson gson = new Gson();
            Gson gson = new GsonBuilder().serializeNulls().create();
//            String kidsInfoJson = gson.toJson (kidsInfo);
            Type listType = new TypeToken<List<KidInfo>>() {}.getType();
            String kidsInfoJson = gson.toJson (kidsInfo, listType);

            Log.d (TAG, kidsInfoJson);

            if (!Utils.saveFile (new File (jsonFile), kidsInfoJson, mActivity.get())) {
                return null;
            }

            List<String> files = new ArrayList<>();
            files.add (jsonFile);
            for (KidInfo kidInfo : kidsInfo) {
                if (kidInfo.getLocalPhotoFile() != null) {
                    files.add (kidInfo.getLocalPhotoFile());
                }
            }

            return zip (files, params[0]);
        }

        public String zip (List<String> files, String user)
        {
            final int BUFFER = 1024;

            SimpleDateFormat sdfDate = new SimpleDateFormat ("yyyyMMdd_HHmmss");
            String strDate = sdfDate.format (new Date());

            String userName;
            if (user.contains ("@")) {
                userName = user.substring (0, user.indexOf ("@"));
            }
            else {
                userName = user;
            }

            File file = new File (mActivity.get().getExternalFilesDir (Environment.DIRECTORY_DOWNLOADS),
                    Tags.APP_NAME);
            if ((!file.exists()) && (!file.mkdirs())) {
                Log.e (TAG, "Directory not created");
                return null;
            }

            String zipFileName = file.getAbsolutePath() + "/" + strDate + "_" + userName + "_backup.zip";
//            Log.d (TAG, "Zip file: " + zipFileName);

            BufferedInputStream origin = null;
            FileOutputStream dest = null;
            ZipOutputStream out = null;
            FileInputStream fis = null;
            int fileCount = 0;
            try {
                dest = new FileOutputStream (zipFileName);
                out = new ZipOutputStream (new BufferedOutputStream (dest));
                byte data[] = new byte[BUFFER];

                for (String filename : files) {
//                    Log.d (TAG, "Adding: " + filename);
                    fis = mActivity.get().openFileInput (filename);
//                    fis = new FileInputStream (filename);
                    origin = new BufferedInputStream (fis, BUFFER);

                    ZipEntry entry = new ZipEntry (filename.substring (filename.lastIndexOf ("/") + 1));
                    out.putNextEntry (entry);
                    int count;

                    while ((count = origin.read (data, 0, BUFFER)) != -1) {
                        out.write (data, 0, count);
                    }
                    origin.close();

                    fileCount++;
                    Log.d (TAG, "Number of added files: " + fileCount);
                }

                out.close();
            }
            catch (Exception e) {
                Log.d (TAG, "Problem in compressing the db content", e);
                return null;
            }
            finally {
                Utils.close (origin);
                Utils.close (dest);
                Utils.close (out);
                Utils.close (fis);
            }

            return zipFileName;
        }

        @Override
        public void onPostExecute (String zipFileName)
        {
            super.onPostExecute (zipFileName);
            notifyFinish (zipFileName != null, zipFileName);
        }
    }

    private void notifyFinish (Boolean success, String zipFileName)
    {
        mActivity.get().notifyProgressUpdate (100, R.string.backupDialogTitle,
                R.string.backupDialogExpl);

        if (success) {
            mActivity.get().notifySuccessfulBackup (zipFileName);
        }
        else {
            mActivity.get().notifyFailedBackup (R.string.backupFailed);
        }
    }
}
