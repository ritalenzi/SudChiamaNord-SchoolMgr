package com.sudchiamanord.schoolmgr.operations.results;

import com.sudchiamanord.schoolmgr.info.KidInfo;

import java.util.List;

/**
 * Created by rita on 12/8/15.
 */
public class DownloadResult extends GeneralResult
{
    public DownloadResult (boolean success, int message, boolean correctSession,
                           List<KidInfo> kidsInfo, String shoolYear)
    {
        super (success, message, correctSession);
        mKidsInfo = kidsInfo;
        mSchoolYear = shoolYear;
    }

    public List<KidInfo> getKidsInfo()
    {
        return mKidsInfo;
    }

    public String getSchoolYear()
    {
        return mSchoolYear;
    }


    private final List<KidInfo> mKidsInfo;
    private final String mSchoolYear;
}
