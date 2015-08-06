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
package com.google.android.gcm.demo.logic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.demo.R;
import com.google.android.gcm.demo.model.Constants;
import com.google.android.gcm.demo.service.LoggingService;
import com.google.android.gcm.demo.service.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gcm.demo.logic.HttpRequest.CONTENT_TYPE_FORM_ENCODED;
import static com.google.android.gcm.demo.logic.HttpRequest.HEADER_CONTENT_TYPE;
import static com.google.android.gcm.demo.model.Constants.REGISTER_IN_SERVER;
import static com.google.android.gcm.demo.model.Constants.REST_PARAM_DEVICE_NAME;
import static com.google.android.gcm.demo.model.Constants.REST_PARAM_REG_ID;
import static com.google.android.gcm.demo.model.Constants.SERVER_URL_ROOT;

/**
 * This class used to register and unregister the app for GCM.
 * Registration involves getting the app's instance id and using it to request a token with
 * the scope {@link GoogleCloudMessaging.INSTANCE_ID_SCOPE} and the audience set to the project's
 * id.
 */
public class InstanceIdHelper {

    private final Context mContext;
    private final LoggingService.Logger mLogger;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "InstanceIdHelper";

    private ThirdPartyServerHelper thirdPartyServer = new ThirdPartyServerHelper();

    public InstanceIdHelper(Context context) {
        mContext = context;
        mLogger = new LoggingService.Logger(mContext);
    }

    /**
     * Register for GCM
     */
    public void getGcmTokenInBackground() {
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(mContext, RegistrationIntentService.class);
        mContext.startService(intent);

    }

    /**
     * Unregister by deleting the token
     */
    public void deleteGcmTokeInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String senderId = mContext.getString(R.string.gcm_MyServer_SenderId);
                String token = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.GCM_TOKEN, "");
                try {
                    if (!token.isEmpty()) {
                        InstanceID.getInstance(mContext).deleteToken(senderId,
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                        mLogger.log(Log.INFO, "delete token succeeded." +
                                "\nsenderId: " + senderId);
                        //Delete from server
                        thirdPartyServer.removeRegistrationFromServer(token);
                    } else {
                        mLogger.log(Log.INFO, "remove token failed." +
                                "\nsenderId: " + senderId + "\nerror: No Token present in preferences, probably you did not get one yet");
                    }
                } catch (final IOException e) {
                    mLogger.log(Log.INFO, "remove token failed." +
                            "\nsenderId: " + senderId + "\nerror: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }


}
