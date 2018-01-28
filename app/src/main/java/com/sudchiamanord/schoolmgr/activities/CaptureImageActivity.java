package com.sudchiamanord.schoolmgr.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

/**
 * Created by rita on 11/22/15.
 */
public class CaptureImageActivity extends Activity
{
    private static final String TAG = CaptureImageActivity.class.getSimpleName();
    private SurfaceView preview = null;
    private Button captureButton;
    private Button okButton;
    private Button cancelButton;
    private SurfaceHolder previewHolder = null;
    private Camera camera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;
    private Picture picture;
    private int anInt;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_capture_image);

        preview = (SurfaceView) findViewById (R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback (surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        captureButton = (Button) findViewById (R.id.capturePhotoButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inPreview) {
                    camera.takePicture (null, null, photoCallback);
                    inPreview = false;
                }
            }
        });

        okButton = (Button) findViewById (R.id.okPhotoButton);
        okButton.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if (picture == null) {
                    new AlertDialog.Builder (CaptureImageActivity.this)
                            .setTitle (R.string.generalError)
                            .setMessage (R.string.photoLoadingError)
                            .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                            {
                                public void onClick (DialogInterface dialog, int which)
                                {
                                    okButton.setVisibility (View.INVISIBLE);
                                    cancelButton.setVisibility (View.INVISIBLE);
                                    captureButton.setVisibility (View.VISIBLE);
                                    camera.startPreview();
                                    inPreview = true;
                                }
                            })
                            .setIcon (android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else {
                    new SavePhotoTask().execute (picture);
                }
            }
        });

        cancelButton = (Button) findViewById (R.id.cancelPhotoButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                captureButton.setVisibility(View.VISIBLE);
                camera.startPreview();
                inPreview = true;
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Camera.CameraInfo info = new Camera.CameraInfo();

            for (int i=0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo (i, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    camera = Camera.open (i);
                }
            }
        }

        if (camera == null) {
            camera = Camera.open();
        }

        startPreview();
    }

    @Override
    public void onPause()
    {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera = null;
        inPreview = false;

        super.onPause();
    }

    private Camera.Size getBestPreviewSize (int width, int height, Camera.Parameters parameters)
    {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {

                if (result == null) {
                    result = size;
                }
                else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    private Camera.Size getSmallestPictureSize (Camera.Parameters parameters)
    {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            }
            else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea < resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }

    private Camera.Size getIntermediatePictureSize (Camera.Parameters parameters)
    {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            Log.d (TAG, "Size: " + size.width + "x" + size.height);
            if ((size.width > Tags.MAX_RESOLUTION_WIDTH) ||
                    (size.height > Tags.MAX_RESOLUTION_HEIGHT)) {
                continue;
            }
            if (result == null) {
                result = size;
            }
            else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    Log.d (TAG, "Candidate Size: " + size.width + "x" + size.height);
                    result = size;
                }
            }
        }

        if (result == null) {
            Log.d (TAG, "No resolution under the default values has been found. Using the smallest one");
            result = getSmallestPictureSize (parameters);
        }

        Log.d (TAG, "Selected Size: " + result.width + "x" + result.height);
        return (result);
    }

    private Camera.Size getBiggetsPictureSize (Camera.Parameters parameters)
    {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            }
            else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }

    private void initPreview (int width, int height)
    {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay (previewHolder);
            }
            catch (Throwable t) {
                Log.e (TAG, "Exception in setPreviewDisplay()", t);
                Toast.makeText (CaptureImageActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int i=0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo (i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    camera.setDisplayOrientation (getCameraOrientation (info));
                }
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, parameters);
//                Camera.Size pictureSize = getSmallestPictureSize (parameters);
//                Camera.Size pictureSize = getBiggetsPictureSize (parameters);
                Camera.Size pictureSize = getIntermediatePictureSize (parameters);

                if (size != null && pictureSize != null) {
                    parameters.setPreviewSize (size.width, size.height);
                    parameters.setPictureSize (pictureSize.width, pictureSize.height);
                    parameters.setPictureFormat (ImageFormat.JPEG);
                    camera.setParameters (parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    private int getCameraOrientation (Camera.CameraInfo info)
    {
        return (info.orientation - getScreenOrientation() + 360) % 360;
    }

    private int getScreenOrientation()
    {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        return degrees;
    }

    private void startPreview()
    {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback()
    {
        public void surfaceCreated (SurfaceHolder holder)
        {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged (SurfaceHolder holder, int format, int width, int height)
        {
            initPreview (width, height);
            startPreview();
        }

        public void surfaceDestroyed (SurfaceHolder holder)
        {
            // no-op
        }
    };

    Camera.PictureCallback photoCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken (byte[] data, Camera camera)
        {
            int orientation = 0;
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int i=0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo (i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    orientation = getCameraOrientation (info);
                    break;
                }
            }
            picture = new Picture (data, orientation);

            okButton.setVisibility (View.VISIBLE);
            cancelButton.setVisibility (View.VISIBLE);
            captureButton.setVisibility (View.INVISIBLE);
        }
    };

    class Picture
    {
        Picture (byte[] jpeg, int rotationAngle)
        {
            this.jpeg = jpeg;
            this.rotationAngle = rotationAngle;
        }

        byte[] jpeg;
        int rotationAngle;

    }

    class SavePhotoTask extends AsyncTask<Picture, String, String>
    {
        @Override
        protected String doInBackground (Picture... picture)
        {
            String tmpPhotoName = "rotated-photo.jpg";
            File tmpPhoto = new File (tmpPhotoName);
            String tmpPhotoAbsoluteName = new File (getFilesDir(), tmpPhotoName).getAbsolutePath();

            File photo = new File (UUID.randomUUID().toString() + "-photo.jpg");

            if (tmpPhoto.exists()) {
                tmpPhoto.delete();
            }

            if (photo.exists()) {
                photo.delete();
            }

            FileOutputStream rfos = null;
            FileOutputStream fos = null;
            try {
                rfos = openFileOutput (tmpPhoto.getPath(), Context.MODE_PRIVATE);
                rfos.write (picture[0].jpeg);

                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile (tmpPhotoAbsoluteName, bounds);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                Bitmap bm = BitmapFactory.decodeFile (tmpPhotoAbsoluteName, opts);

                // Rotate Bitmap
                Matrix matrix = new Matrix();
                matrix.setRotate (picture[0].rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                Bitmap rotatedBitmap = Bitmap.createBitmap (bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

                // Write result on file
                fos = openFileOutput (photo.getPath(), Context.MODE_PRIVATE);
                rotatedBitmap.compress (Bitmap.CompressFormat.JPEG, 100, fos);
            }
            catch (java.io.IOException e) {
                Log.e (TAG, "Exception in saving the file on the device", e);
                return null;
            }
            finally {
                Utils.close (rfos);
                Utils.close (fos);
            }

            if (tmpPhoto.exists()) {
                tmpPhoto.delete();
            }

            return photo.getName();
        }

        @Override
        protected void onPostExecute (String photoFileName)
        {
            if (photoFileName == null) {
                notifySaveFailed();
            }
            else {
                notifySaveSuccess (photoFileName);
            }
        }
    }

    private void notifySaveFailed()
    {
        new AlertDialog.Builder (CaptureImageActivity.this)
                .setTitle (R.string.generalError)
                .setMessage (R.string.photoSavingError)
                .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                        okButton.setVisibility (View.INVISIBLE);
                        cancelButton.setVisibility (View.INVISIBLE);
                        captureButton.setVisibility (View.VISIBLE);
                        camera.startPreview();
                        inPreview = true;
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void notifySaveSuccess (String photoFileName)
    {
        Intent resIntent = new Intent();
        resIntent.putExtra (Tags.PHOTO_FILE_NAME, photoFileName);
        setResult (RESULT_OK, resIntent);
        finish();
    }
}
