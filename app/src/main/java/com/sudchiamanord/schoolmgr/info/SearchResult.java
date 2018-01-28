package com.sudchiamanord.schoolmgr.info;

/**
 * Created by rita on 1/24/16.
 */
public class SearchResult
{
    public SearchResult (KidInfo kidInfo, String schoolYear)
    {
        mKidInfo = kidInfo;
        mSchoolYear = schoolYear;
    }

    public KidInfo getKidInfo()
    {
        return mKidInfo;
    }

    public String getSchoolYear()
    {
        return mSchoolYear;
    }

    private final String mSchoolYear;
    private final KidInfo mKidInfo;
}
