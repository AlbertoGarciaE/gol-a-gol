/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.google.android.gcm.GolAGol.logic;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.service.RegistrationIntentService;

public class InstanceIdHelper {

    private final Context mContext;
   // private final LoggingService.Logger mLogger;
   // private SharedPreferences sharedPreferences;
    //private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "InstanceIdHelper";

    private ThirdPartyServerHelper thirdPartyServer = new ThirdPartyServerHelper();

    public InstanceIdHelper(Context context) {
        mContext = context;
        //mLogger = new LoggingService.Logger(mContext);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Register for GCM
     */
    public void getGcmTokenInBackground(String name) {
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(mContext, RegistrationIntentService.class);
        intent.setAction(RegistrationIntentService.REGISTRATION_I_S_ACTION_GET_TOKEN);
        intent.putExtra(Constants.REST_PARAM_DEVICE_NAME, name);
        mContext.startService(intent);

    }

    /**
     * Unregister by deleting the token
     */
    public void deleteGcmTokeInBackground() {
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(mContext, RegistrationIntentService.class);
        intent.setAction(RegistrationIntentService.REGISTRATION_I_S_ACTION_ERASE_TOKEN);
        mContext.startService(intent);
    }


}
