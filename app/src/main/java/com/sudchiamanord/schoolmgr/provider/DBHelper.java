package com.sudchiamanord.schoolmgr.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.info.ListaAdozioni;
import com.sudchiamanord.schoolmgr.provider.DBContract.KidEntry;
import com.sudchiamanord.schoolmgr.provider.DBContract.AdoptionEntry;

import java.io.File;

/**
 * Created by rita on 12/3/15.
 */
public class DBHelper extends SQLiteOpenHelper
{
    /**
     * If the database schema is changed, the database version must be incremented.
     */
    private static final int DATABASE_VERSION = 4;

    /**
     * Database name.
     */
    public static final String DATABASE_NAME = "schoolMgr.db";


    public DBHelper (Context context)
    {
        super (context, context.getCacheDir() + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db)
    {
        db.execSQL (
                "CREATE TABLE "
                        + KidEntry.KID_TABLE + " ("
                        + KidEntry._ID + " INTEGER PRIMARY KEY, "
                        + KidEntry.COLUMN_SERVER_ID + " INTEGER, "
                        + KidEntry.COLUMN_STATUS + " INTEGER, "
                        + KidEntry.COLUMN_KID_NAME + " TEXT NOT NULL, "
                        + KidEntry.COLUMN_KID_LASTNAME + " TEXT, "
                        + KidEntry.COLUMN_KID_NICKNAME + " TEXT, "
                        + KidEntry.COLUMN_SEX + " INTEGER NOT NULL, "
                        + KidEntry.COLUMN_BDAY + " TEXT NOT NULL, " // YYYY-MM-DD
                        + KidEntry.COLUMN_ADDRESS + " TEXT, "
                        + KidEntry.COLUMN_CITY + " TEXT, "
                        + KidEntry.COLUMN_PHONE + " TEXT, "
                        + KidEntry.COLUMN_DAD_ALIVE + " INTEGER, " // 0 (false) and 1 (true)
                        + KidEntry.COLUMN_DAD_NAME + " TEXT, "
                        + KidEntry.COLUMN_DAD_LASTNAME + " TEXT, "
                        + KidEntry.COLUMN_DAD_NICKNAME + " TEXT, "
                        + KidEntry.COLUMN_DAD_JOB + " TEXT, "
                        + KidEntry.COLUMN_MOM_ALIVE + " INTEGER, " // 0 (false) and 1 (true)
                        + KidEntry.COLUMN_MOM_NAME + " TEXT, "
                        + KidEntry.COLUMN_MOM_LASTNAME + " TEXT, "
                        + KidEntry.COLUMN_MOM_NICKNAME + " TEXT, "
                        + KidEntry.COLUMN_MOM_JOB + " TEXT, "
                        + KidEntry.COLUMN_PHOTO_NAME + " TEXT, "
                        + KidEntry.COLUMN_NOTES + " TEXT, "
                        + KidEntry.COLUMN_CREATE_DATE + " TEXT, "   // timestamp
                        + KidEntry.COLUMN_CREATE_BY + " TEXT, "
                        + KidEntry.COLUMN_SCHOOL_ID + " INTEGER, "
                        + KidEntry.COLUMN_SCHOOL_YEAR + " TEXT, "
                        + KidEntry.COLUMN_CLASS + " INTEGER, "
                        + KidEntry.COLUMN_LAST_UPDATE_DATE + " TEXT NOT NULL, "   // timestamp
                        + KidEntry.COLUMN_LAST_UPDATE_USER + " TEXT NOT NULL, "
                        + KidEntry.COLUMN_LOCAL_PHOTO_FILE + " TEXT"
                        + ");");

        db.execSQL (
                "CREATE TABLE "
                        + AdoptionEntry.ADOPTION_TABLE + " ("
                        + AdoptionEntry._ID + " INTEGER PRIMARY KEY, "
                        + AdoptionEntry.COLUMN_PAYMENT_CODE + " TEXT, "
                        + AdoptionEntry.COLUMN_CLASS + " TEXT, "
                        + AdoptionEntry.COLUMN_ADOPTION_CODE + " TEXT, "
                        + AdoptionEntry.COLUMN_SCHOOL_ID + " TEXT, "
                        + AdoptionEntry.COLUMN_SCHOOL_NAME + " TEXT, "
                        + AdoptionEntry.COLUMN_SCHOOL_CITY + " TEXT, "
                        + AdoptionEntry.COLUMN_ADOPTION_PREFIX + " TEXT, "
                        + AdoptionEntry.COLUMN_ADOPTER_ID + " TEXT, "
                        + AdoptionEntry.COLUMN_ADOPTER_NAME + " TEXT, "
                        + AdoptionEntry.COLUMN_ADOPTER_LASTNAME + " TEXT, "
                        + AdoptionEntry.COLUMN_SCHOOL_YEAR + " TEXT, "
                        + AdoptionEntry.COLUMN_KID_ID + " INTEGER, "
                        + "FOREIGN KEY (" + AdoptionEntry.COLUMN_KID_ID + ") "
                        + "REFERENCES " + KidEntry.KID_TABLE + "(" + KidEntry._ID + ")"
                        + ");");
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This database is only a cache for online data, so its upgrade policy is simply to discard
        // the data and start over. This method only fires if you change the version number for your
        // database. It does NOT depend on the version number for your application. If the schema is
        // updated without wiping data, commenting out the next 2 lines should be the top priority
        // before modifying this method
        db.execSQL ("DROP TABLE IF EXISTS " + KidEntry.KID_TABLE);
        db.execSQL ("DROP TABLE IF EXISTS " + AdoptionEntry.ADOPTION_TABLE);
        // TODO: DELETE THE OTHER TWO TABLES
        onCreate(db);
    }

    public static ContentValues getKidContentValues (KidInfo kidInfo)
    {
        ContentValues cv = new ContentValues();
        cv.put (KidEntry.COLUMN_SERVER_ID, kidInfo.getIdado());
        cv.put (KidEntry.COLUMN_STATUS, kidInfo.getStato());    // 0 not active, 1 active
        cv.put (KidEntry.COLUMN_KID_NAME, kidInfo.getAnome());
        cv.put (KidEntry.COLUMN_KID_LASTNAME, kidInfo.getAcogn());
        cv.put (KidEntry.COLUMN_KID_NICKNAME, kidInfo.getAnick());
        cv.put (KidEntry.COLUMN_SEX, kidInfo.getAdsex());
        cv.put (KidEntry.COLUMN_BDAY, kidInfo.getDatan());
        cv.put (KidEntry.COLUMN_ADDRESS, kidInfo.getIndir());
        cv.put (KidEntry.COLUMN_CITY, kidInfo.getCitta());
        cv.put (KidEntry.COLUMN_PHONE, kidInfo.getAdtel());
        cv.put (KidEntry.COLUMN_DAD_ALIVE, kidInfo.getIsDadAliveAsInt());   // 0 (false) and 1 (true)
        cv.put (KidEntry.COLUMN_DAD_NAME, kidInfo.getPnome());
        cv.put (KidEntry.COLUMN_DAD_LASTNAME, kidInfo.getPcogn());
        cv.put (KidEntry.COLUMN_DAD_NICKNAME, kidInfo.getPnick());
        cv.put (KidEntry.COLUMN_DAD_JOB, kidInfo.getDBPajob());
        cv.put (KidEntry.COLUMN_MOM_ALIVE, kidInfo.getIsMomAliveAsInt());   // 0 (false) and 1 (true)
        cv.put (KidEntry.COLUMN_MOM_NAME, kidInfo.getMnome());
        cv.put (KidEntry.COLUMN_MOM_LASTNAME, kidInfo.getMcogn());
        cv.put (KidEntry.COLUMN_MOM_NICKNAME, kidInfo.getMnick());
        cv.put (KidEntry.COLUMN_MOM_JOB, kidInfo.getDBMajob());
        cv.put (KidEntry.COLUMN_PHOTO_NAME, kidInfo.getFfoto());
        cv.put (KidEntry.COLUMN_NOTES, kidInfo.getAnote());
        cv.put (KidEntry.COLUMN_CREATE_DATE, kidInfo.getCreil());
        cv.put (KidEntry.COLUMN_CREATE_BY, kidInfo.getCreda());
        cv.put (KidEntry.COLUMN_SCHOOL_ID, kidInfo.getScuol());
        cv.put (KidEntry.COLUMN_SCHOOL_YEAR, kidInfo.getAnnos());
        cv.put (KidEntry.COLUMN_CLASS, kidInfo.getSclas());
        cv.put (KidEntry.COLUMN_LAST_UPDATE_DATE, kidInfo.getLastt());
        cv.put (KidEntry.COLUMN_LAST_UPDATE_USER, kidInfo.getLastu());
        cv.put (KidEntry.COLUMN_LOCAL_PHOTO_FILE, kidInfo.getLocalPhotoFile());

        return cv;
    }

    public static ContentValues getAdoptionInfoValues (ListaAdozioni adoptionInfo, int kidDBId)
    {
        ContentValues cv = new ContentValues();
        cv.put (AdoptionEntry.COLUMN_PAYMENT_CODE, adoptionInfo.getIdpag());
        cv.put (AdoptionEntry.COLUMN_CLASS, adoptionInfo.getPclas());
        cv.put (AdoptionEntry.COLUMN_ADOPTION_CODE, adoptionInfo.getCodic());
        cv.put (AdoptionEntry.COLUMN_SCHOOL_ID, adoptionInfo.getIdscu());
        cv.put (AdoptionEntry.COLUMN_SCHOOL_NAME, adoptionInfo.getScuno());
        cv.put (AdoptionEntry.COLUMN_SCHOOL_CITY, adoptionInfo.getScuci());
        cv.put (AdoptionEntry.COLUMN_ADOPTION_PREFIX, adoptionInfo.getScupr());
        cv.put (AdoptionEntry.COLUMN_ADOPTER_ID, adoptionInfo.getAddid());
        cv.put (AdoptionEntry.COLUMN_ADOPTER_NAME, adoptionInfo.getAdnom());
        cv.put (AdoptionEntry.COLUMN_ADOPTER_LASTNAME, adoptionInfo.getAdcog());
        cv.put (AdoptionEntry.COLUMN_SCHOOL_YEAR, adoptionInfo.getAnnos());
        cv.put (AdoptionEntry.COLUMN_KID_ID, kidDBId);

        return cv;
    }
}
