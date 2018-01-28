package com.sudchiamanord.schoolmgr.util.scheleton;

import android.os.Bundle;
import android.util.Log;


public class GenericActivity<OpsType extends ConfigurableOps> extends LifecycleLoggingActivity
{
    protected final String TAG = getClass().getSimpleName();

    /**
     * Used to retain the OpsType state between runtime configuration changes.
     */
    private final RetainedFragmentManager mRetainedFragmentManager = new RetainedFragmentManager (
            this.getFragmentManager(), TAG);

    /**
     * Instance of the operations ("Ops") type.
     */
    private OpsType mOpsInstance;

    /**
     * Lifecycle hook method that's called when this Activity is created
     * @param savedInstanceState Object that contains saved state information.
     * @param opsType Class object that's used to create an operations ("Ops") object.
     */
    public void onCreate (Bundle savedInstanceState, Class<OpsType> opsType)
    {
        // Call up to the super class.
        super.onCreate (savedInstanceState);

        try {
            // Handle configuration-related events, including the initial creation of an Activity
            // and any subsequent runtime configuration changes.
            handleConfiguration(opsType);
        }
        catch (Exception e) {
            Log.d(TAG, "handleConfiguration " + e);
            // Propagate this as a runtime exception.
            throw new RuntimeException (e);
        }
    }

    /**
     * Handle hardware (re)configurations, such as rotating the display
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void handleConfiguration (Class<OpsType> opsType) throws InstantiationException,
            IllegalAccessException
    {
        // If this method returns true it's the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d (TAG, "First time onCreate() call");

            // Initialize the GenericActivity fields.
            initialize (opsType);
        }
        else {
            // The RetainedFragmentManager was previously initialized, which means that a runtime
            // configuration change occurred.
            Log.d (TAG, "Second or subsequent onCreate() call");

            // Try to obtain the OpsType instance from the
            // RetainedFragmentManager.
            mOpsInstance = mRetainedFragmentManager.get (opsType.getSimpleName());

            // This check shouldn't be necessary under normal circumstances, but it's better to
            // lose state than to crash!
            if (mOpsInstance == null)
                // Initialize the GenericActivity fields.
                initialize (opsType);
            else
                // Inform it that the runtime configuration change has
                // completed.
                mOpsInstance.onConfiguration (this,  false);
        }
    }

    /**
     * Initialize the GenericActivity fields.
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void initialize (Class<OpsType> opsType) throws InstantiationException,
            IllegalAccessException
    {
        // Create the OpsType object.
        mOpsInstance = opsType.newInstance();

        // Put the OpsInstance into the RetainedFragmentManager under the simple name.
        mRetainedFragmentManager.put (opsType.getSimpleName(), mOpsInstance);

        // Perform the first initialization.
        mOpsInstance.onConfiguration (this, true);
    }

    /**
     * Return the initialized OpsType instance for use by the application.
     */
    public OpsType getOps()
    {
        return mOpsInstance;
    }

    /**
     * Return the initialized OpsType instance for use by the application.
     */
    public RetainedFragmentManager getRetainedFragmentManager()
    {
        return mRetainedFragmentManager;
    }
}
