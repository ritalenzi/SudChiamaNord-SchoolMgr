package com.sudchiamanord.schoolmgr.provider.query;

import android.net.Uri;

/**
 * Created by rita on 1/10/16.
 */
public class Query
{
    public Query (Uri uri, String[] projection, String whereClause, String[] whereArgs,
                  String sortOrder)
    {
        this.uri = uri;
        this.projection = projection;
        this.whereClause = whereClause;
        this.whereArgs = whereArgs;
        this.sortOrder = sortOrder;
    }

    public final Uri uri;
    public final String[] projection;
    public final String whereClause;
    public final String[] whereArgs;
    public final String sortOrder;
}
