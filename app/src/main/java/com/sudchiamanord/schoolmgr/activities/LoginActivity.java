package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.io.File;
import java.util.Properties;

public class LoginActivity extends Activity
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mUser;
    private EditText mPassword;
    private CheckBox mRemember;
//    private String mUser;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_login);

        mUser = (EditText) findViewById (R.id.loginUser);
        mPassword = (EditText) findViewById (R.id.loginPassword);
        mPassword.setTransformationMethod (new PasswordTransformationMethod());
        mPassword.setSelection (mPassword.getSelectionStart(), mPassword.getSelectionEnd());

        mRemember = (CheckBox) findViewById (R.id.loginRememberUserAndPassword);

//        mRemember.setOnClickListener (new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkPasswordFormat();
//            }
//        });

        Properties properties = Utils.getProperties (getFileStreamPath (ConfigActivity.CONFIG_FILE));
        String savedUser = properties.getProperty (Tags.SAVED_USER);
        if ((savedUser != null) && (!savedUser.isEmpty())) {
            mUser.setText (savedUser);
            mPassword.setText (properties.getProperty(Tags.SAVED_PW));
        }

//        mUser.setText ("ritamichele.lenzi@gmail.com");  // TODO: REMOVE!!
//        mPassword.setText ("bonazza");  // TODO: REMOVE!!
    }

    @Override
    public void onBackPressed()
    {
        setResult (Tags.RESULT_LOGIN_UNCHANGED, new Intent());
        finish();
    }

//    private void checkPasswordFormat()
//    {
//        int start = mPassword.getSelectionStart();
//        int end = mPassword.getSelectionEnd();
//        if (mShowPw.isChecked()) {
//            mPassword.setTransformationMethod (null);
//        }
//        else {
//            mPassword.setTransformationMethod (new PasswordTransformationMethod());
//        }
//        mPassword.setSelection (start, end);
//    }

    public void hideKeyboard (Activity activity, IBinder windowToken)
    {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService (
                Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow (windowToken, 0);
    }

    public void login (View view)
    {
        hideKeyboard (this, mUser.getWindowToken());
        hideKeyboard (this, mPassword.getWindowToken());

        String user = mUser.getText().toString();
        String pw = mPassword.getText().toString();
        if ((user.isEmpty()) || (pw.isEmpty())) {
            Toast.makeText (this, getString (R.string.nullUserAndPwMsg), Toast.LENGTH_SHORT).show();
            return;
        }

        Properties properties = Utils.getProperties (getFileStreamPath (ConfigActivity.CONFIG_FILE),
                R.string.confFileReadError, this);

        // Check user
        String currentUserProperty = null;
        for (String property : properties.stringPropertyNames()) {
            if (user.equals (properties.getProperty (property))) {
                currentUserProperty = property;
                break;
            }
        }
        if (currentUserProperty == null) {
            Utils.generateErrorDialog (R.string.wrongUser, this);
            return;
        }

        // Check password
        String uuid = currentUserProperty.replace (Tags.USER, "");
        String currentPwProperty = Tags.PW + uuid;
        if (properties.getProperty (currentPwProperty) == null) {
            Utils.generateErrorDialog (R.string.wrongPassword, this);
            return;
        }

        if (!pw.equals (properties.getProperty (currentPwProperty))) {
            Utils.generateErrorDialog (R.string.wrongPassword, this);
            return;
        }

        // Remembering user and password
        if (mRemember.isChecked()) {
            properties.setProperty (Tags.SAVED_USER, user);
            properties.setProperty(Tags.SAVED_PW, pw);
            Utils.updateConfig (new File (ConfigActivity.CONFIG_FILE), properties, this);
        }


        Intent resIntent = new Intent();
        resIntent.putExtra (Tags.USER, user);
        setResult (RESULT_OK, resIntent);
        finish();
    }
}
