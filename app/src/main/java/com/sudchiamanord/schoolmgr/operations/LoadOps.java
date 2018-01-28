package com.sudchiamanord.schoolmgr.operations;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.activities.LoadActivity;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.provider.DBContract;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

import org.apache.http.util.ByteArrayBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by rita on 1/17/16.
 */
public class LoadOps
{
    private final String TAG = getClass().getSimpleName();

    private WeakReference<LoadActivity> mActivity;

    public LoadOps (Activity activity, Uri uri)
    {
        mActivity = new WeakReference<>((LoadActivity) activity);
//        mActivity.get().initProgressDialog (R.string.loadDialogTitle, R.string.loadDialogExpl);

        Uri[] params = new Uri[1];
        params[0] = uri;
        new LoadAsyncTask().execute (params);
    }

    class LoadAsyncTask extends AsyncTask<Uri, Integer, Integer>
    {
        @Override
        protected Integer doInBackground (Uri... params)
        {
            Uri uri = params[0];
            InputStream is = null;
            ZipInputStream zin = null;
            FileOutputStream fos = null;

            List<KidInfo> kidsInfo = null;
            try {
                is = mActivity.get().getContentResolver().openInputStream (uri);
                zin = new ZipInputStream (is);
                ZipEntry ze;

                // Loading the kidsInfo.json
                while ((ze = zin.getNextEntry()) != null) {
                    if (ze.getName().equals (BackupOps.jsonFile)) {
                        Gson gson = new Gson();
                        StringBuilder sb = new StringBuilder();
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            sb.append ((char) c);
                        }
                        Log.d (TAG, sb.toString());
                        kidsInfo = gson.fromJson (sb.toString(),
                                new TypeToken<List<KidInfo>>(){}.getType());
                        Log.d (TAG, "Number of kids: " + kidsInfo.size());
                        break;
                    }

//                    fos = new FileOutputStream (_location + ze.getName());
//                    for (int c = zin.read(); c != -1; c = zin.read()) {
//                        fout.write(c);
//                    }

                    zin.closeEntry();
//                    fos.close();
                }
                Utils.close (is);
                Utils.close (zin);

                if (kidsInfo == null) {
                    return null;
                }

                if (kidsInfo.size() == 0) {
                    return kidsInfo.size();
                }

                final int finalSize = kidsInfo.size();
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.get().initProgressDialog (finalSize, R.string.loadDialogTitle,
                                R.string.loadDialogExpl);
                    }
                });

                // Deleting the info on the database
                int rowDeleted = mActivity.get().getContentResolver().delete (
                        DBContract.KidEntry.KID_CONTENT_URI, null, null);
                Log.d (TAG, "Deleted " + rowDeleted + " rows from " + DBContract.KidEntry.KID_TABLE);
                rowDeleted = mActivity.get().getContentResolver().delete (
                        DBContract.AdoptionEntry.ADOPTION_CONTENT_URI, null, null);
                Log.d (TAG, "Deleted " + rowDeleted + " rows from " + DBContract.AdoptionEntry.ADOPTION_TABLE);
                int imagesDeleted = Utils.deleteAllImages (mActivity.get());
                Log.d (TAG, "Deleted " + imagesDeleted + " images");

//                // Copying the images
                is = mActivity.get().getContentResolver().openInputStream (uri);
                zin = new ZipInputStream (is);
                int count = 0;
                while ((ze = zin.getNextEntry()) != null) {
                    if (ze.getName().equals (BackupOps.jsonFile)) {
                        continue;
                    }
                    ByteArrayBuffer baf = new ByteArrayBuffer (50);
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        baf.append ((byte) c);
                    }
//                    Log.d (TAG, "Image file " + ze.getName());
                    byte[] imageData = baf.toByteArray();
//                    Log.d (TAG, "Image file " + ze.getName() + " as bytes");
                    Bitmap bitmap = BitmapFactory.decodeByteArray (imageData, 0, imageData.length);
                    fos = mActivity.get().openFileOutput (ze.getName(), Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    count++;
                    Log.d (TAG, "Loaded " + count + " info");
                    mActivity.get().runOnUiThread (new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mActivity.get().notifyProgressUpdate (R.string.loadDialogExpl);
                        }
                    });
                }

                count = 0;
                for (KidInfo kidInfo : kidsInfo) {
                    if (Utils.insert (mActivity.get(), kidInfo)) {
                        count++;
                        mActivity.get().runOnUiThread (new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mActivity.get().notifyProgressUpdate (R.string.storeInDBDialogExpl);
                            }
                        });
                    }
                }
                Log.d (TAG, "Added to the database " + count + " kids info");

            }
            catch (IOException e) {
                Log.d (TAG, "Problem in loading the info", e);
                return null;
            }
            finally {
                Utils.close (is);
                Utils.close (zin);
                Utils.close (fos);
            }

            return kidsInfo.size();
        }

        @Override
        public void onPostExecute (Integer nInfo)
        {
            super.onPostExecute(nInfo);
            notifyFinish(nInfo != null, nInfo);
        }
    }

    private void notifyFinish (Boolean success, Integer nInfo)
    {
        if (success) {
            mActivity.get().notifySuccessfulLoad (nInfo);
        }
        else {
            mActivity.get().notifyFailedLoad (R.string.loadFailed);
        }
    }
}
