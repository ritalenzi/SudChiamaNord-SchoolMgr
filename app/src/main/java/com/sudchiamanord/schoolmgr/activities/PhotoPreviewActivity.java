package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by rita on 1/24/16.
 */
public class PhotoPreviewActivity extends Activity
{
    private static final String TAG = PhotoPreviewActivity.class.getSimpleName();

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_photo_preview);

        ImageView imageView = (ImageView) findViewById (R.id.photoPreview);

        String photoFileName = getIntent().getStringExtra (Tags.PHOTO_FILE_NAME);
        FileInputStream fis = null;
        try {
            File filePath = this.getFileStreamPath (photoFileName);
            fis = new FileInputStream (filePath);
            Bitmap bitmap = BitmapFactory.decodeStream (fis);
            Log.d (TAG, "Photo size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            imageView.setImageBitmap (bitmap);
        }
        catch (Exception e) {
            Log.e (TAG, "Problem in loading the image", e);
            Toast.makeText (getApplicationContext(), getString (R.string.photoPreviewError),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        finally {
            Utils.close (fis);
        }
    }
}
