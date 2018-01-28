package com.sudchiamanord.schoolmgr.operations.results;

/**
 * Created by rita on 12/10/15.
 */
public class ImageDownloadResult extends GeneralResult
{
    public ImageDownloadResult (boolean success, int message, boolean correctSession,
                                String imageFilename)
    {
        super (success, message, correctSession);
        mImageFilename = imageFilename;
    }

    public String getImageFilename()
    {
        return mImageFilename;
    }

    private final String mImageFilename;
}
