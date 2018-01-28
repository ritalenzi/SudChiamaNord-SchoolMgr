package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.info.ListaAdozioni;
import com.sudchiamanord.schoolmgr.operations.SaveInfoOps;
import com.sudchiamanord.schoolmgr.util.fragments.DatePickerFragment;
import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by rita on 11/11/15.
 */
public class KidInfoActivity extends FragmentActivity
{
    private static final String TAG = KidInfoActivity.class.getSimpleName();

    private RingProgressDialog mOpProgressDialog;

    private EditText mKidNameEditText;
    private EditText mKidLastnameExitText;
    private EditText mKidSecondNameEditText;
    private Spinner mStateSpinner;
    private Spinner mSexSpinner;
    private ImageView mPhotoThumbnail;
    private Button mChangePictureButton;
    private Button mDeletePictureButton;
    private EditText mBirthdayEditText;
    private EditText mAddressEditText;
    private EditText mCityEditText;
    private EditText mPhoneEditText;

    private Spinner mDadAliveSpinner;
    private EditText mDadNameEditText;
    private EditText mDadLastnameEditText;
    private EditText mDadSecondNameEditText;
    private EditText mDadJobEditText;

    private Spinner mMomAliveSpinner;
    private EditText mMomNameEditText;
    private EditText mMomLastnameEditText;
    private EditText mMomSecondNameEditText;
    private EditText mMomJobEditText;

    private EditText mSchoolEditText;
    private EditText mClassEditText;
    private Spinner mYearSpinner;

    private EditText mCreatedByEditText;
    private EditText mCreatedOnEditText;

    private EditText mNotesEditText;

    private boolean mEditable = true;
    private KidInfo mKidInfo;
    private String mUser;
    private String mPhotoFile;

    private int[] mSchoolCodes;
    private String[] mClasses;
    private int mSelectedYearIndex;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_kid_info);

        mOpProgressDialog = new RingProgressDialog (KidInfoActivity.this);

        if (savedInstanceState != null) {
            mEditable = savedInstanceState.getBoolean (Tags.EDITABLE_COMPONENTS);
            mKidInfo = savedInstanceState.getParcelable (Tags.KID_INFO);
            mUser = savedInstanceState.getString (Tags.CURRENT_USER);
            mSchoolCodes = savedInstanceState.getIntArray (Tags.SCHOOL_CODES_ARRAY);
            mClasses = savedInstanceState.getStringArray (Tags.CLASSES_ARRAY);
        }
        else {
            mKidInfo = getIntent().getParcelableExtra (Tags.KID_INFO);
            mUser = getIntent().getStringExtra (Tags.CURRENT_USER);
        }

        Properties properties = Utils.getProperties (getFileStreamPath (ConfigActivity.CONFIG_FILE),
                R.string.confFileReadError, this);
        String schoolYear = properties.getProperty (Tags.SCHOOL_YEAR);


        // Kid's info
        mKidNameEditText = (EditText) findViewById (R.id.kidNameEditText);
        mKidLastnameExitText = (EditText) findViewById (R.id.kidLastnameEditText);
        mKidSecondNameEditText = (EditText) findViewById (R.id.kidSecondNameEditText);

        final EditText adoptedLabel = (EditText) findViewById (R.id.adoptedLabel);
        Button adoptionsDetailsButton = (Button) findViewById (R.id.adoptionsDetailsButton);
        adoptionsDetailsButton.setEnabled (false);

        mStateSpinner = (Spinner) findViewById (R.id.stateSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource (this, R.array.state,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateSpinner.setAdapter(adapter);

        mSexSpinner = (Spinner) findViewById (R.id.sexSpinner);
        adapter = ArrayAdapter.createFromResource (this, R.array.sex,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSexSpinner.setAdapter (adapter);

        // Kid's photo
        mPhotoThumbnail = (ImageView) findViewById (R.id.photoThumbnail);
        mPhotoThumbnail.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View arg0)
            {
                if (mPhotoFile != null) {
                    Intent intent = new Intent(getApplicationContext(), PhotoPreviewActivity.class);
                    intent.putExtra (Tags.PHOTO_FILE_NAME, mPhotoFile);
                    startActivity (intent);
                }
            }
        });
        mChangePictureButton = (Button) findViewById (R.id.changePictureButton);
        mDeletePictureButton = (Button) findViewById (R.id.deletePictureButton);

        // Kid's bday
        mBirthdayEditText = (EditText) findViewById (R.id.birthdayEditText);

        // Address
        mAddressEditText = (EditText) findViewById (R.id.addressEditText);
        mCityEditText = (EditText) findViewById (R.id.cityEditText);
        mPhoneEditText = (EditText) findViewById (R.id.phoneEditText);

        // Dad's info
        mDadAliveSpinner = (Spinner) findViewById (R.id.dadAliveSpinner);
        adapter = ArrayAdapter.createFromResource (this, R.array.alive,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDadAliveSpinner.setAdapter(adapter);
        mDadAliveSpinner.setSelection (1);

        mDadNameEditText = (EditText) findViewById (R.id.dadNameEditText);
        mDadLastnameEditText = (EditText) findViewById (R.id.dadLastnameEditText);
        mDadSecondNameEditText = (EditText) findViewById (R.id.dadSecondNameEditText);
        mDadJobEditText = (EditText) findViewById (R.id.dadJobEditText);

        // Mom's info
        mMomAliveSpinner = (Spinner) findViewById (R.id.momAliveSpinner);
        adapter = ArrayAdapter.createFromResource (this, R.array.alive,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMomAliveSpinner.setAdapter(adapter);
        mMomAliveSpinner.setSelection (1);

        mMomNameEditText = (EditText) findViewById (R.id.momNameEditText);
        mMomLastnameEditText = (EditText) findViewById (R.id.momLastnameEditText);
        mMomSecondNameEditText = (EditText) findViewById (R.id.momSecondNameEditText);
        mMomJobEditText = (EditText) findViewById (R.id.momJobEditText);

        // School
        mSchoolEditText = (EditText) findViewById (R.id.schoolEditText);
        mClassEditText = (EditText) findViewById (R.id.classEditText);
        mYearSpinner = (Spinner) findViewById (R.id.yearSpinner);
        adapter = ArrayAdapter.createFromResource (this, R.array.school_years,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        mYearSpinner.setAdapter (adapter);
        mYearSpinner.setSelection (adapter.getPosition (schoolYear));
        if (mSchoolCodes == null) {
            mSchoolCodes = new int[mYearSpinner.getAdapter().getCount()];
            for (int i=0; i<mYearSpinner.getAdapter().getCount(); i++) {
                mSchoolCodes[i] = -1;
            }
        }
        if (mClasses == null) {
            mClasses = new String[mYearSpinner.getAdapter().getCount()];
        }
        mSelectedYearIndex = mYearSpinner.getSelectedItemPosition();

        mYearSpinner.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected (AdapterView<?> parent, View selectedItemView, int pos, long id)
            {
                if ((mSchoolEditText.getText() == null) ||
                        (mSchoolEditText.getText().toString().isEmpty())) {
                    mSchoolCodes[mSelectedYearIndex] = -1;
                }
                else {
                    mSchoolCodes[mSelectedYearIndex] = Integer.parseInt (
                            mSchoolEditText.getText().toString());
                }

                if ((mClassEditText.getText() == null) ||
                        (mClassEditText.getText().toString().isEmpty())) {
                    mClasses[mSelectedYearIndex] = null;
                }
                else {
                    mClasses[mSelectedYearIndex] = mClassEditText.getText().toString();
                }

                mSelectedYearIndex = pos;

                if (mSchoolCodes[mSelectedYearIndex] != -1) {
                    mSchoolEditText.setText (String.valueOf (mSchoolCodes[mSelectedYearIndex]));
                }
                else {
                    mSchoolEditText.setText ("");
                }
                String schoolClass = mClasses[mSelectedYearIndex];
                if ((schoolClass != null) && (!schoolClass.equals ("?"))) {
                    mClassEditText.setText (schoolClass);
                }
                else {
                    mClassEditText.setText ("");
                }
            }

            @Override
            public void onNothingSelected (AdapterView<?> parentView)
            {
            }

        });

        // Created by and creation date
        mCreatedByEditText = (EditText) findViewById (R.id.createdByEditText);
        mCreatedByEditText.setClickable (false);
        mCreatedByEditText.setFocusableInTouchMode (false);
        mCreatedByEditText.setFocusable (false);
        mCreatedByEditText.setText (mUser);
        mCreatedOnEditText = (EditText) findViewById (R.id.createdOnEditText);
        mCreatedOnEditText.setClickable (false);
        mCreatedOnEditText.setFocusableInTouchMode (false);
        mCreatedOnEditText.setFocusable (false);

        // Notes
        mNotesEditText = (EditText) findViewById (R.id.notesEditText);

        if (mKidInfo == null) {
            return;
        }

        mEditable = false;
        enableComponents (false);

        mKidNameEditText.setText (mKidInfo.getAnome());
        mKidLastnameExitText.setText(mKidInfo.getAcogn());
        mKidSecondNameEditText.setText(mKidInfo.getAnick());

        if (schoolYear != null) {
            boolean adoptionFound = false;
            if (mKidInfo.getListaAdozioni() != null) {
                for (ListaAdozioni adoption : mKidInfo.getListaAdozioni()) {
                    if (schoolYear.equals (adoption.getAnnos())) {
                        adoptionFound = true;
                        break;
                    }
                }
                adoptionsDetailsButton.setEnabled (true);
            }
            else {
                adoptionsDetailsButton.setEnabled (false);
            }
            adoptedLabel.setText (adoptionFound ? R.string.adoptedLabel : R.string.notAdoptedLabel);
            ((ImageView) findViewById (R.id.adoptedImageView)).setImageResource (adoptionFound ?
                    R.drawable.set2_ok_icon_64 : R.drawable.set2_cancel_icon_64);
        }
        else {
            adoptedLabel.setText (R.string.unknownAdoptionStatusLabel);
            ((ImageView) findViewById (R.id.adoptedImageView)).setImageResource (
                    R.drawable.set2_cancel_icon_64);
        }
        adoptionsDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdoptionsListActivity.class);
                intent.putParcelableArrayListExtra (Tags.ADOPTIONS_LIST_INFO,
                        (ArrayList<? extends Parcelable>) mKidInfo.getListaAdozioni());
                startActivity (intent);
            }
        });

        mStateSpinner.setSelection (mKidInfo.getStato());
        mSexSpinner.setSelection (mKidInfo.getAdsex());

        if (mKidInfo.getLocalPhotoFile() != null) {
            setPhoto (mKidInfo.getLocalPhotoFile());
        }

        mBirthdayEditText.setText (mKidInfo.getDatan());

        mAddressEditText.setText (mKidInfo.getIndir());
        mCityEditText.setText (mKidInfo.getCitta());
        mPhoneEditText.setText (mKidInfo.getAdtel());

        mDadAliveSpinner.setSelection (mKidInfo.getIsDadAliveAsInt());
        mDadNameEditText.setText (mKidInfo.getPnome());
        mDadLastnameEditText.setText (mKidInfo.getPcogn());
        mDadSecondNameEditText.setText (mKidInfo.getPnick());
        mDadJobEditText.setText (mKidInfo.getPajob());

        mMomAliveSpinner.setSelection (mKidInfo.getIsMomAliveAsInt());
        mMomNameEditText.setText (mKidInfo.getMnome());
        mMomLastnameEditText.setText (mKidInfo.getMcogn());
        mMomSecondNameEditText.setText (mKidInfo.getMnick());
        mMomJobEditText.setText (mKidInfo.getMajob());

        if (mKidInfo.getListaAdozioni() != null) {
            for (int i=0; i<mYearSpinner.getAdapter().getCount(); i++) {
                String year = (String) mYearSpinner.getAdapter().getItem (i);
                for (ListaAdozioni adoption : mKidInfo.getListaAdozioni()) {
                    if (adoption.getAnnos() == null) {
                        mSchoolCodes[i] = -1;
                        mClasses[i] = null;
                        continue;
                    }

                    if (adoption.getAnnos().equals (year)) {
                        mSchoolCodes[i] = adoption.getIdscu();
                        mClasses[i] = adoption.getPclas();
                    }
                }
            }
        }
        if (mSchoolCodes[mSelectedYearIndex] != -1) {
            mSchoolEditText.setText (String.valueOf (mSchoolCodes[mSelectedYearIndex]));
        }
        String schoolClass = mClasses[mSelectedYearIndex];
        if ((schoolClass != null) && (!schoolClass.equals ("?"))) {
            mClassEditText.setText (schoolClass);
        }

        mCreatedByEditText.setText (mKidInfo.getCreda());
        mCreatedOnEditText.setText (mKidInfo.getCreil());

        mNotesEditText.setText (mKidInfo.getAnote());
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putBoolean (Tags.EDITABLE_COMPONENTS, mEditable);
        outState.putParcelable (Tags.KID_INFO, mKidInfo);
        outState.putString (Tags.CURRENT_USER, mUser);
        outState.putIntArray (Tags.SCHOOL_CODES_ARRAY, mSchoolCodes);
        outState.putStringArray(Tags.CLASSES_ARRAY, mClasses);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        if (!mEditable) {
            getMenuInflater().inflate(R.menu.action_edit, menu);
        }
        getMenuInflater().inflate (R.menu.action_save_changes, menu);
        getMenuInflater().inflate (R.menu.action_discard_changes, menu);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId()) {

            case R.id.action_edit:
                mEditable = true;
                enableComponents (true);
                break;

            case R.id.action_save_changes:
                if (!mEditable) {
                    return true;
                }

                saveChanges();

                break;

            case R.id.action_discard_changes:
                if (!mEditable) {
                    return true;
                }
                checkDiscardChanges();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (!mEditable) {
            finish();
        }
        else {
            checkDiscardChanges();
        }
    }

    private void enableComponents (boolean b)
    {
        mKidNameEditText.setClickable(b);
        mKidNameEditText.setFocusableInTouchMode(b);
        mKidNameEditText.setFocusable(b);
        mKidNameEditText.requestFocus();

        mKidLastnameExitText.setClickable(b);
        mKidLastnameExitText.setFocusableInTouchMode(b);
        mKidLastnameExitText.setFocusable(b);

        mKidSecondNameEditText.setClickable(b);
        mKidSecondNameEditText.setFocusableInTouchMode(b);
        mKidSecondNameEditText.setFocusable(b);

        mStateSpinner.setEnabled(b);

        mSexSpinner.setEnabled (b);

        mChangePictureButton.setEnabled (b);
        mDeletePictureButton.setEnabled (b);

//        mBirthdayEditText.setClickable (b);
//        mBirthdayEditText.setFocusableInTouchMode (b);
//        mBirthdayEditText.setFocusable (b);
        mBirthdayEditText.setEnabled (b);

        mAddressEditText.setClickable (b);
        mAddressEditText.setFocusableInTouchMode (b);
        mAddressEditText.setFocusable (b);

        mCityEditText.setClickable (b);
        mCityEditText.setFocusableInTouchMode (b);
        mCityEditText.setFocusable (b);

        mPhoneEditText.setClickable (b);
        mPhoneEditText.setFocusableInTouchMode (b);
        mPhoneEditText.setFocusable (b);

        mDadAliveSpinner.setEnabled (b);

        mDadNameEditText.setClickable (b);
        mDadNameEditText.setFocusableInTouchMode (b);
        mDadNameEditText.setFocusable (b);

        mDadLastnameEditText.setClickable(b);
        mDadLastnameEditText.setFocusableInTouchMode(b);
        mDadLastnameEditText.setFocusable(b);

        mDadSecondNameEditText.setClickable(b);
        mDadSecondNameEditText.setFocusableInTouchMode(b);
        mDadSecondNameEditText.setFocusable(b);

        mDadJobEditText.setClickable (b);
        mDadJobEditText.setFocusableInTouchMode (b);
        mDadJobEditText.setFocusable (b);

        mMomAliveSpinner.setEnabled (b);

        mMomNameEditText.setClickable (b);
        mMomNameEditText.setFocusableInTouchMode (b);
        mMomNameEditText.setFocusable (b);

        mMomLastnameEditText.setClickable(b);
        mMomLastnameEditText.setFocusableInTouchMode(b);
        mMomLastnameEditText.setFocusable(b);

        mMomSecondNameEditText.setClickable(b);
        mMomSecondNameEditText.setFocusableInTouchMode(b);
        mMomSecondNameEditText.setFocusable(b);

        mMomJobEditText.setClickable (b);
        mMomJobEditText.setFocusableInTouchMode (b);
        mMomJobEditText.setFocusable (b);

        mSchoolEditText.setClickable (b);
        mSchoolEditText.setFocusableInTouchMode (b);
        mSchoolEditText.setFocusable (b);

        mClassEditText.setClickable (b);
        mClassEditText.setFocusableInTouchMode (b);
        mClassEditText.setFocusable (b);

        mYearSpinner.setEnabled (b);

        mNotesEditText.setClickable (b);
        mNotesEditText.setFocusableInTouchMode (b);
        mNotesEditText.setFocusable (b);
    }

    public void showDatePicker (View view)
    {
        DatePickerFragment date = new DatePickerFragment();
        int year, month, day;

        if ((mBirthdayEditText.getText() != null) &&
                (!mBirthdayEditText.getText().toString().isEmpty()) &&
                (!mBirthdayEditText.getText().toString().equals ("0000-00-00"))) {
            StringTokenizer st = new StringTokenizer (mBirthdayEditText.getText().toString(), "-");
            year = Integer.parseInt (st.nextToken());
            month = Integer.parseInt (st.nextToken()) - 1;
            day = Integer.parseInt (st.nextToken());
        }
        else {
            Calendar calender = Calendar.getInstance();
            year = calender.get (Calendar.YEAR);
            month = calender.get (Calendar.MONTH);
            day = calender.get (Calendar.DAY_OF_MONTH);
        }

        Bundle args = new Bundle();
        args.putInt ("year", year);
        args.putInt ("month", month);
        args.putInt ("day", day);
        date.setArguments(args);

        date.setCallBack(callback);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Calendar date = Calendar.getInstance();
            date.set (Calendar.YEAR, year);
            date.set (Calendar.MONTH, monthOfYear);
            date.set (Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
            mBirthdayEditText.setText (dateFormat.format(date.getTime()));
//            mBirthdayEditText.setText (String.valueOf (year) + "-" +
//                    String.valueOf (monthOfYear + 1) + "-" + String.valueOf (dayOfMonth));
        }
    };

    public void changePicture (View view)
    {
        Intent intent = new Intent();
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectPicture)),
                Tags.PHOTO_LOAD_REQUEST);
    }

    public void deletePicture (View view)
    {
        if (mKidInfo == null) {
            return;
        }

        String photoFile = mKidInfo.getLocalPhotoFile();
        if (photoFile == null) {
            return;
        }
        boolean deleted = deleteFile (photoFile);
        if (deleted) {
            mPhotoThumbnail.setImageDrawable (getResources().getDrawable (
                    R.drawable.empty_picture_icon_128));
            mPhotoFile = null;
        }
        Log.d (TAG, "File " + (deleted ? "deleted" : "not deleted"));
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {

        if (requestCode == Tags.PHOTO_LOAD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getStringExtra (Tags.PHOTO_FILE_NAME) != null) {
                    String photoFileName = data.getStringExtra (Tags.PHOTO_FILE_NAME);
                    setPhoto (photoFileName);
                }
                else {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex (filePathColumn[0]);
                    String picturePath = cursor.getString (columnIndex);
                    cursor.close();

                    FileOutputStream fos = null;
                    String photoFileName = UUID.randomUUID().toString() + "-photo.jpg";
                    try {
                        fos = openFileOutput (photoFileName, Context.MODE_PRIVATE);
                        Bitmap bitmap = BitmapFactory.decodeFile (picturePath);
                        Log.d (TAG, "Photo initial size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                        if ((bitmap.getWidth() > Tags.MAX_RESOLUTION_WIDTH) ||
                                (bitmap.getHeight() > Tags.MAX_RESOLUTION_HEIGHT)) {
                            int newWidth;
                            int newHeight;
                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                newWidth = Tags.DEFAULT_RESOLUTION_WIDTH;
                                newHeight = Tags.DEFAULT_RESOLUTION_HEIGHT;
                            }
                            else if (bitmap.getWidth() < bitmap.getHeight()) {
                                newWidth = Tags.DEFAULT_RESOLUTION_HEIGHT;
                                newHeight = Tags.DEFAULT_RESOLUTION_WIDTH;
                            }
                            else {
                                newWidth = Tags.DEFAULT_RESOLUTION_WIDTH;
                                newHeight = newWidth;
                            }
                            bitmap = Bitmap.createScaledBitmap (bitmap, newWidth, newHeight, true);
                        }
                        Log.d (TAG, "Photo final size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                        bitmap.compress (Bitmap.CompressFormat.JPEG, 100, fos);
                    }
                    catch (java.io.IOException e) {
                        Log.e (TAG, "Exception in saving the file on the device", e);
                        return;
                    }
                    finally {
                        Utils.close(fos);
                    }
                    setPhoto (photoFileName);
                }
            }
        }
    }

    private void setPhoto (String photoFileName)
    {
        FileInputStream fis = null;
        try {
            File filePath = this.getFileStreamPath (photoFileName);
            fis = new FileInputStream (filePath);
            Bitmap thumbnail = BitmapFactory.decodeStream (fis);
            mPhotoThumbnail.setImageBitmap (thumbnail);
            mPhotoFile = photoFileName;
        }
        catch (Exception e) {
            Log.e (TAG, "getThumbnail() on internal storage", e);
        }
        finally {
            Utils.close (fis);
        }
    }

    private void saveChanges()
    {
        String kidName = mKidNameEditText.getText().toString();
        String kidFirstLastname = mKidLastnameExitText.getText().toString();
        if ((kidName.isEmpty()) || (kidFirstLastname.isEmpty())) {
            new AlertDialog.Builder (KidInfoActivity.this)
                    .setTitle (R.string.generalError)
                    .setMessage (R.string.nullKidName)
                    .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                    {
                        public void onClick (DialogInterface dialog, int which)
                        {
                        }
                    })
                    .setIcon (android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }

        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format (date.getTime());
        Log.d (TAG, "Time: " + time);


        if (mKidInfo == null) {
            mKidInfo = new KidInfo();
            mKidInfo.setCreda (mUser);
            mKidInfo.setCreil (time);
        }
        mKidInfo.setLastu (mUser);
        mKidInfo.setLastt (time);

        mKidInfo.setAnome (kidName);
        mKidInfo.setAcogn (kidFirstLastname);
        mKidInfo.setAnick (mKidSecondNameEditText.getText().toString());
        mKidInfo.setStato (mStateSpinner.getSelectedItemPosition());
        mKidInfo.setAdsex (mSexSpinner.getSelectedItemPosition());

        mKidInfo.setLocalPhotoFile (mPhotoFile);

        mKidInfo.setDatan (mBirthdayEditText.getText().toString());
        mKidInfo.setIndir (mAddressEditText.getText().toString());
        mKidInfo.setCitta (mCityEditText.getText().toString());
        mKidInfo.setAdtel (mPhoneEditText.getText().toString());

        mKidInfo.setPnome (mDadNameEditText.getText().toString());
        mKidInfo.setPcogn (mDadLastnameEditText.getText().toString());
        mKidInfo.setPnick (mDadSecondNameEditText.getText().toString());
        if (mDadAliveSpinner.getSelectedItemPosition() == 0) {
            mKidInfo.setDadAlive (false);
            mKidInfo.setPajob ((String) mDadAliveSpinner.getSelectedItem());
        }
        else {
            mKidInfo.setDadAlive (true);
            mKidInfo.setPajob (mDadJobEditText.getText().toString());
        }

        mKidInfo.setMnome (mMomNameEditText.getText().toString());
        mKidInfo.setMcogn (mMomLastnameEditText.getText().toString());
        mKidInfo.setMnick (mMomSecondNameEditText.getText().toString());
        if (mMomAliveSpinner.getSelectedItemPosition() == 0) {
            mKidInfo.setMomAlive (false);
            mKidInfo.setMajob ((String) mMomAliveSpinner.getSelectedItem());
        }
        else {
            mKidInfo.setMomAlive (true);
            mKidInfo.setMajob (mMomJobEditText.getText().toString());
        }

        mKidInfo.setAnote (mNotesEditText.getText().toString());

        if ((mSchoolEditText.getText() == null) ||
                (mSchoolEditText.getText().toString().isEmpty())) {
            mSchoolCodes[mSelectedYearIndex] = -1;
        }
        else {
            mSchoolCodes[mSelectedYearIndex] = Integer.parseInt (
                    mSchoolEditText.getText().toString());
        }

        if ((mClassEditText.getText() == null) ||
                (mClassEditText.getText().toString().isEmpty())) {
            mClasses[mSelectedYearIndex] = null;
        }
        else {
            mClasses[mSelectedYearIndex] = mClassEditText.getText().toString();
        }

        Log.d (TAG, "mSelectedYearIndex: " + mSelectedYearIndex);
        Log.d (TAG, "mSchoolCodes[mSelectedYearIndex]: " + mSchoolCodes[mSelectedYearIndex]);
        Log.d (TAG, "mClasses[mSelectedYearIndex]: " + mClasses[mSelectedYearIndex]);

        if (mKidInfo.getListaAdozioni() == null) {
            for (int i=0; i<mYearSpinner.getAdapter().getCount(); i++) {
                boolean add = false;
                ListaAdozioni adoption = new ListaAdozioni();
                adoption.setAnnos ((String) mYearSpinner.getAdapter().getItem (i));
                if (mSchoolCodes[i] != -1) {
                    adoption.setIdscu (mSchoolCodes[i]);
                    add = true;
                }
                if (mClasses[i] != null) {
                    adoption.setPclas (mClasses[i]);
                    add = true;
                }
                if (add) {
                    mKidInfo.addAdoption (adoption);
                }

                Log.d (TAG, "First if - year: " + adoption.getAnnos());
                Log.d (TAG, "First if - year: " + adoption.getIdscu());
                Log.d (TAG, "First if - year: " + adoption.getPclas());
            }
        }
        else {
            for (int i=0; i<mYearSpinner.getAdapter().getCount(); i++) {
                String spinnerYear = (String) mYearSpinner.getAdapter().getItem (i);
                ListaAdozioni adoption = mKidInfo.getAdoption (spinnerYear);
                if (adoption == null) {
                    boolean add = false;
                    if (mSchoolCodes[i] != -1) {
                        adoption = new ListaAdozioni();
                        adoption.setIdscu (mSchoolCodes[i]);
                        add = true;
                    }
                    if (mClasses[i] != null) {
                        if (adoption == null) {
                            adoption = new ListaAdozioni();
                        }
                        adoption.setPclas (mClasses[i]);
                        add = true;
                    }
                    if (add) {
                        adoption.setAnnos (spinnerYear);
                        mKidInfo.addAdoption (adoption);
                    }
                }
                else {
                    if (mSchoolCodes[i] != -1) {
                        adoption.setIdscu (mSchoolCodes[i]);
                    }
                    if (mClasses[i] != null) {
                        adoption.setPclas (mClasses[i]);
                    }
                }

                if (adoption != null) {
                    Log.d (TAG, "Second if - year: " + adoption.getAnnos());
                    Log.d (TAG, "Second if - year: " + adoption.getIdscu());
                    Log.d (TAG, "Second if - year: " + adoption.getPclas());
                }
            }

            Log.d (TAG, "N. adoptions: " + mKidInfo.getListaAdozioni().size());
        }

        new SaveInfoOps (this, mKidInfo);
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog(progress, dialogTitle, dialogExpl);
    }

    public void notifySuccess()
    {
        Toast.makeText (this, R.string.dbUpdateSuccessful, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void notifyFail()
    {
        new AlertDialog.Builder (KidInfoActivity.this)
                .setTitle(R.string.generalError)
                .setMessage(R.string.dbUpdateFail)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void checkDiscardChanges()
    {
        new AlertDialog.Builder (KidInfoActivity.this)
                .setTitle (R.string.discardChangesDialogTitle)
                .setMessage (R.string.discardChangesDialogMessage)
                .setPositiveButton (android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton (android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_alert)
                .show();
    }
}
