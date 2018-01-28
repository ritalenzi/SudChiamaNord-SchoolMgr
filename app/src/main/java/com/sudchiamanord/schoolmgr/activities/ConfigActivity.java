package com.sudchiamanord.schoolmgr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.util.Tags;
import com.sudchiamanord.schoolmgr.util.Utils;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by rita on 12/7/15.
 */
public class ConfigActivity extends Activity
{
    private static final String TAG = ConfigActivity.class.getSimpleName();

    static final String CONFIG_FILE = "config.properties";

    private EditText mServerAddrEditText;
//    private ImageView mServerUserRegisteredImageView;
//    private TextView mServerUserRegisteredTextView;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_config);

        Properties properties = Utils.getProperties (this.getFileStreamPath (CONFIG_FILE),
                R.string.confFileReadError, this);

        String serverAddress = properties.getProperty (Tags.SERVER_ADDRESS);
        mServerAddrEditText = (EditText) findViewById (R.id.serverEditText);
        mServerAddrEditText.setText (serverAddress);

//        mServerAddrEditText.setText ("http://www.sudchiamanord.com/home/scholarships_test/app/manager.php");
    }

    public void updateServerAddress (View view)
    {
        hideKeyboard (this, mServerAddrEditText.getWindowToken());

        if ((mServerAddrEditText.getText() == null) ||
                (mServerAddrEditText.getText().toString().isEmpty())) {
            Utils.generateErrorDialog (R.string.noServerAddressError, this);
        }
        else {
//            mServerUserRegisteredTextView.setText (R.string.serverNoUserRegisteredLabel);

            Properties properties = Utils.getProperties (this.getFileStreamPath (CONFIG_FILE),
                    R.string.confFileReadError, this);
            properties.setProperty (Tags.SERVER_ADDRESS, mServerAddrEditText.getText().toString());
            Utils.updateConfig (new File (CONFIG_FILE), properties, this);

            Toast.makeText (this, R.string.serverAddressCorrectlyUpdated, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard (Activity activity, IBinder windowToken)
    {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService (
                Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public void registerUser (View view)
    {
        Properties properties = Utils.getProperties (this.getFileStreamPath (CONFIG_FILE),
                R.string.confFileReadError, this);
        String serverAddress = properties.getProperty (Tags.SERVER_ADDRESS);
        if (serverAddress == null) {
            Toast.makeText (this, R.string.noServerAddressError, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent (this, RegisterUserActivity.class);
        intent.putExtra (Tags.SERVER_ADDRESS, serverAddress);
        startActivityForResult (intent, Tags.REGISTER_USER_REQUEST);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Tags.REGISTER_USER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
//                mServerUserRegisteredImageView.setImageDrawable (getResources().getDrawable (
//                        R.drawable.set2_ok_icon_64));
//                mServerUserRegisteredTextView.setText (R.string.serverUserRegisteredLabel);
                String user = data.getStringExtra (Tags.USER);
                String pw = data.getStringExtra (Tags.PW);
                String schoolYear = data.getStringExtra (Tags.SCHOOL_YEAR);

                Properties properties = Utils.getProperties (this.getFileStreamPath (CONFIG_FILE),
                        R.string.confFileReadError, this);

                String currentUserProperty = null;
                String currentPwProperty;
                for (String property : properties.stringPropertyNames()) {
                    if (properties.getProperty (property).equals (user)) {
                        currentUserProperty = property;
                        break;
                    }
                }
                if (currentUserProperty != null) {
                    String uuid = currentUserProperty.replace (Tags.USER, "");
                    currentPwProperty = Tags.PW + uuid;
                }
                else {
                    String uuid = UUID.randomUUID().toString();
                    currentUserProperty = Tags.USER + uuid;
                    currentPwProperty = Tags.PW + uuid;
                }

                properties.setProperty (currentUserProperty, user);
                properties.setProperty (currentPwProperty, pw);
                properties.setProperty (Tags.SCHOOL_YEAR, schoolYear);
                Utils.updateConfig (new File (CONFIG_FILE), properties, this);
            }
            else if (resultCode == Tags.RESULT_REGISTER_USER_FAILED) {
//                mServerUserRegisteredImageView.setImageDrawable (getResources().getDrawable (
//                        R.drawable.set2_cancel_icon_64));
//                mServerUserRegisteredTextView.setText (R.string.serverNoUserRegisteredLabel);
                String user = data.getStringExtra (Tags.USER);

                Properties properties = Utils.getProperties (this.getFileStreamPath (CONFIG_FILE),
                        R.string.confFileReadError, this);

                String currentUserProperty = null;
                String currentPwProperty = null;
                for (String property : properties.stringPropertyNames()) {
                    if (properties.getProperty (property).equals (user)) {
                        currentUserProperty = property;
                        break;
                    }
                }
                if (currentUserProperty != null) {
                    String uuid = currentUserProperty.replace (Tags.USER, "");
                    properties.remove (currentUserProperty);
                    currentPwProperty = Tags.PW + uuid;
                }
                if (currentPwProperty != null) {
                    properties.remove (currentPwProperty);
                }

                Utils.updateConfig (new File (CONFIG_FILE), properties, this);
            }
            else {
                Log.d (TAG, "Cancelled Register User operation");
            }
        }
    }
}
