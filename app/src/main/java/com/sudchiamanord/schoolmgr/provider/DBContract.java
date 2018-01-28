package com.sudchiamanord.schoolmgr.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rita on 12/5/15.
 */
public class DBContract
{/**
 * The "Content authority" is a name for the entire content provider, similar to the relationship
 * between a domain name and its website.  A convenient string to use for the content authority
 * is the package name for the app, which must be unique on the device.
 */
public static final String CONTENT_AUTHORITY = "com.sudchiamanord.schoolmgr.provider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's that apps will use to contact the
     * content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse ("content://" + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible URI's), e.g.,
     * content://nord.chiama.sud.caccia/stage/ is a valid path for stage data. However,
     * content://nord.chiama.sud.caccia/givemeroot/ will fail since the ContentProvider hasn't been
     * given any information on what to do with "givemeroot".
     */
    public static final String PATH_KID = KidEntry.KID_TABLE;
    public static final String PATH_ADOPTION = AdoptionEntry.ADOPTION_TABLE;
//    public static final String PATH_SCHOOL = SchooleEntry.SCHOOL_TABLE_NAME;  // TODO
//    public static final String PATH_USER = UserEntry.USER_TABLE_NAME;     // TODO


    public static abstract class KidEntry implements BaseColumns
    {
        public static final String KID_TABLE = "kid";

        public static final Uri KID_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath (
                PATH_KID).build();

        public static final String COLUMN_SERVER_ID = "serverId";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_KID_NAME = "kidName";
        public static final String COLUMN_KID_LASTNAME = "kidLastname";
        public static final String COLUMN_KID_NICKNAME = "kidNickname";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_BDAY = "bday";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_DAD_ALIVE = "dadAlive";   // 0 (false) and 1 (true)
        public static final String COLUMN_DAD_NAME = "dadName";
        public static final String COLUMN_DAD_LASTNAME = "dadLastname";
        public static final String COLUMN_DAD_NICKNAME = "dadNickname";
        public static final String COLUMN_DAD_JOB = "dadJob";
        public static final String COLUMN_MOM_ALIVE = "momAlive";
        public static final String COLUMN_MOM_NAME = "momName";     // 0 (false) and 1 (true)
        public static final String COLUMN_MOM_LASTNAME = "momLastname";
        public static final String COLUMN_MOM_NICKNAME = "momNickname";
        public static final String COLUMN_MOM_JOB = "momJob";
        public static final String COLUMN_PHOTO_NAME = "photoName";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_CREATE_DATE = "createDate";
        public static final String COLUMN_CREATE_BY = "createBy";
        public static final String COLUMN_SCHOOL_ID = "schoolId";
        public static final String COLUMN_SCHOOL_YEAR = "schoolYear";
        public static final String COLUMN_CLASS = "class";
        public static final String COLUMN_LAST_UPDATE_DATE = "lastUpdateDate";
        public static final String COLUMN_LAST_UPDATE_USER = "lastUpdateUser";
        public static final String COLUMN_LOCAL_PHOTO_FILE = "localPhotoFile";


        /**
         * Return a Uri that points to the row containing a given id
         * @param id row id
         * @return Uri uri corresponding to the given id
         */
        public static Uri buildUri (Long id)
        {
            return ContentUris.withAppendedId (KID_CONTENT_URI, id);
        }

        public static Long getRowFromURI (Uri uri)
        {
            if (uri == null) {
                return (long) -1;
            }

            String row = uri.toString().replace (KID_CONTENT_URI.toString(), "");
            row = row.replace ("/", "");
            try {
                return Long.parseLong (row);
            }
            catch (NumberFormatException e) {
                return (long) -1;
            }
        }
    }

    public static abstract class AdoptionEntry implements BaseColumns
    {
        public static final String ADOPTION_TABLE = "adoption";

        public static final Uri ADOPTION_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath (
                ADOPTION_TABLE).build();

        public static final String COLUMN_PAYMENT_CODE = "paymentCode";         // "idpag"
        public static final String COLUMN_CLASS = "class";                      // "pclas"
        public static final String COLUMN_ADOPTION_CODE = "adoptionCode";       // "codic"
        public static final String COLUMN_SCHOOL_ID = "schoolId";               // "idscu"
        public static final String COLUMN_SCHOOL_NAME = "schoolName";           // "scuno"
        public static final String COLUMN_SCHOOL_CITY = "schoolCity";           // "scuci"
        public static final String COLUMN_ADOPTION_PREFIX = "adoptionPrefix";   // "scupr"
        public static final String COLUMN_ADOPTER_ID = "adopterId";             // "addid"
        public static final String COLUMN_ADOPTER_NAME = "adopterName";         // "adnom"
        public static final String COLUMN_ADOPTER_LASTNAME = "adopterLastname"; // "adcog"
        public static final String COLUMN_SCHOOL_YEAR = "schoolYear";           // "annos"
        public static final String COLUMN_KID_ID = "kidId";                     // foreign key


        /**
         * Return a Uri that points to the row containing a given id
         * @param id row id
         * @return Uri uri corresponding to the given id
         */
        public static Uri buildUri (Long id)
        {
            return ContentUris.withAppendedId (ADOPTION_CONTENT_URI, id);
        }
    }

}
