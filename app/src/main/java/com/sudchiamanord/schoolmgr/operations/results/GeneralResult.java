package com.sudchiamanord.schoolmgr.operations.results;

/**
 * Created by rita on 12/10/15.
 */
public class GeneralResult
{
    public GeneralResult (boolean success, int message, boolean correctSession)
    {
        mSuccess = success;
        mMessage = message;
        mCorrectSession = correctSession;
    }

    public boolean isSuccessful()
    {
        return mSuccess;
    }

    public int getMessage()
    {
        return mMessage;
    }

    public boolean isSessionCorrect()
    {
        return mCorrectSession;
    }

    private final boolean mSuccess;
    private final int mMessage;
    private final boolean mCorrectSession;
}
