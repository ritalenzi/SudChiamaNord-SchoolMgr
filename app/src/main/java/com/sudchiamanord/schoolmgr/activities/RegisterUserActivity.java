package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.operations.RegisterOps;
import com.sudchiamanord.schoolmgr.util.scheleton.GenericActivity;
import com.sudchiamanord.schoolmgr.util.RingProgressDialog;
import com.sudchiamanord.schoolmgr.util.Tags;

/**
 * Created by rita on 12/12/15.
 */
public class RegisterUserActivity extends GenericActivity<RegisterOps>
{
    private static final String TAG = RegisterUserActivity.class.getSimpleName();

    private EditText mUser;
    private EditText mPassword;
    private CheckBox mShowPw;

    private String mServerAddress;

    private RingProgressDialog mOpProgressDialog;


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_register_user);

        mServerAddress = getIntent().getStringExtra (Tags.SERVER_ADDRESS);

        mOpProgressDialog = new RingProgressDialog (RegisterUserActivity.this);

        mUser = (EditText) findViewById (R.id.serverRegisterUser);
        mPassword = (EditText) findViewById (R.id.serverRegisterPassword);
        mShowPw = (CheckBox) findViewById (R.id.serverRegisterShowPassword);

        mShowPw.setOnClickListener (new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                checkPasswordFormat();
            }
        });

        super.onCreate (savedInstanceState, RegisterOps.class);
    }

    private void checkPasswordFormat()
    {
        int start = mPassword.getSelectionStart();
        int end = mPassword.getSelectionEnd();
        if (mShowPw.isChecked()) {
            mPassword.setTransformationMethod (null);
        }
        else {
            mPassword.setTransformationMethod (new PasswordTransformationMethod());
        }
        mPassword.setSelection (start, end);
    }

    public void hideKeyboard (Activity activity, IBinder windowToken)
    {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService (
                Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow (windowToken, 0);
    }

    public void registerUser (View view)
    {
        hideKeyboard (this, mUser.getWindowToken());
        hideKeyboard (this, mPassword.getWindowToken());

        String user = mUser.getText().toString();
        String pw = mPassword.getText().toString();

        if ((user.isEmpty()) || (pw.isEmpty())) {
            Toast.makeText (this, getString (R.string.nullUserAndPwMsg), Toast.LENGTH_SHORT).show();
            return;
        }

        if ((mServerAddress == null) || (mServerAddress.isEmpty())) {
            Toast.makeText (this, getString (R.string.noServerAddressError), Toast.LENGTH_SHORT).show();
            return;
        }

        getOps().register (user, pw, mServerAddress);
    }

    public void notifyProgressUpdate (int progress, int dialogTitle, int dialogExpl)
    {
        mOpProgressDialog.updateProgressDialog (progress, dialogTitle, dialogExpl);
    }

    public void notifySuccessfulRegistration (String user, String pw, String schoolYear)
    {
        Intent resIntent = new Intent();
//        resIntent.putExtra (Tags.SESSION_KEY, sessionKey);
        resIntent.putExtra (Tags.SCHOOL_YEAR, schoolYear);
        resIntent.putExtra (Tags.USER, user);
        resIntent.putExtra (Tags.PW, pw);
        setResult (RESULT_OK, resIntent);
        finish();
    }

    public void notifyFailedRegistration (final String user, int message)
    {
        new AlertDialog.Builder (RegisterUserActivity.this)
                .setTitle (R.string.generalError)
                .setMessage (message)
                .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                        Intent resIntent = new Intent();
                        resIntent.putExtra (Tags.USER, user);
                        setResult (Tags.RESULT_REGISTER_USER_FAILED, resIntent);
                        finish();
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_alert)
                .show();
    }
}
