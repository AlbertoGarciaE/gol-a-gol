package com.google.android.gcm.GolAGol.logic;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.service.RegistrationIntentService;

public class InstanceIdHelper {

    private final Context mContext;
    private static final String TAG = "InstanceIdHelper";

    public InstanceIdHelper(Context context) {
        mContext = context;
    }

    /**
     * Register for GCM
     */
    public void getGcmTokenInBackground(String name) {
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(mContext, RegistrationIntentService.class);
        intent.setAction(Constants.ACTION_REGISTRATION_GET_TOKEN);
        intent.putExtra(Constants.REST_PARAM_DEVICE_NAME, name);
        mContext.startService(intent);

    }

    /**
     * Unregister by deleting the token
     */
    public void deleteGcmTokeInBackground() {
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(mContext, RegistrationIntentService.class);
        intent.setAction(Constants.ACTION_REGISTRATION_ERASE_TOKEN);
        mContext.startService(intent);
    }


}
