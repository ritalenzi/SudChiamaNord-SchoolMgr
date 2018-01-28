package com.sudchiamanord.schoolmgr.operations.mediator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.operations.results.ImageDownloadResult;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by rita on 12/10/15.
 */
class ImageProxy
{
    private final String TAG = ImageProxy.class.getSimpleName();

    private final HttpURLConnection httpConn;
    private final Context mContext;

    ImageProxy (String imageURL, Context context) throws IOException
    {
        mContext = context;

        URL url = new URL (imageURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("GET");
        httpConn.setConnectTimeout (20000);
    }

    void request() throws IOException
    {
        httpConn.connect();
    }

    public ImageDownloadResult getResponse() throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        Log.d(TAG, "Response Code: " + responseCode);

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException ("Received response code " + responseCode + " instead of " +
                    HttpURLConnection.HTTP_OK);
        }

        File photo = new File (UUID.randomUUID().toString() + "-photo.jpg");

        InputStream is = httpConn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream (is, 8190);

        ByteArrayBuffer baf = new ByteArrayBuffer (50);
        int current = 0;
        while ((current = bis.read()) != -1) {
            baf.append ((byte) current);
        }
        byte[] imageData = baf.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray (imageData, 0, imageData.length);

        FileOutputStream fos = null;
        try {
            // Write result on file
            fos = mContext.openFileOutput (photo.getPath(), Context.MODE_PRIVATE);
            bitmap.compress (Bitmap.CompressFormat.JPEG, 100, fos);
        }
        catch (java.io.IOException e) {
            Log.e (TAG, "Exception in saving the file on the device", e);
            return new ImageDownloadResult (true, R.string.imageDownloadFailed, true, null);
        }
        finally {
            Utils.close (fos);
        }

        return new ImageDownloadResult (true, R.string.imageDownloadSuccessful, true, photo.getName());
    }
}
