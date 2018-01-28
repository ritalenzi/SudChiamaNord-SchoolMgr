package com.sudchiamanord.schoolmgr.util;


import android.app.ProgressDialog;
import android.content.Context;

public class BarProgressDialog extends ProgressDialog
{
    private static final String TAG = BarProgressDialog.class.getSimpleName();

    private int mOpProgress;
    public int mMax;
    private Context mContext;

    public BarProgressDialog (Context context)
    {
        super(context);
        mContext = context;
    }

    public void initialize (int max, int dialogTitle, int dialogExpl)
    {
        mOpProgress = 0;
        mMax = max;
        setCanceledOnTouchOutside (false);
        setProgressStyle (STYLE_HORIZONTAL);
        setProgress (mOpProgress);
        setMax (mMax);
        setTitle (dialogTitle);
        setMessage (mContext.getString (dialogExpl));
        show();
    }

    public void updateProgressDialog (int dialogExpl)
    {
        mOpProgress++;
        if (mOpProgress == mMax) {
            dismiss();
            return;
        }

        setProgress (mOpProgress);
        setMessage (mContext.getString (dialogExpl));
    }
}
