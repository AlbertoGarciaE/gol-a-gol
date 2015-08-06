/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gcm.demo.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.demo.R;
import com.google.android.gcm.demo.model.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.Random;

import com.google.android.gcm.demo.logic.ThirdPartyServerHelper;

public class RegistrationIntentService extends IntentService {

    /**
     * Initial delay before first retry, without jitter.
     */
    protected static final int BACKOFF_INITIAL_DELAY = 1000;
    /**
     * Maximum delay before a retry.
     */
    protected static final int MAX_BACKOFF_DELAY = 1024000;

    protected final Random random = new Random();
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    private ThirdPartyServerHelper thirdPartyServer = new ThirdPartyServerHelper();

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                int attempt = 0;
                int retries = 5;
                int backoff = BACKOFF_INITIAL_DELAY;
                String token;
                boolean tryAgain;
                // Retry using exponential backoff if we fail to retrieve the Registration token
                do {
                    attempt++;
                    Log.i(TAG, "Attempt #" + attempt + " to GCM Registration Token");
                    // [START get_token]
                    InstanceID instanceID = InstanceID.getInstance(this);
                    token = instanceID.getToken(getString(R.string.gcm_MyServer_SenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    // [END get_token]
                    tryAgain = token == null && attempt <= retries;
                    if (tryAgain) {
                        int sleepTime = backoff / 2 + random.nextInt(backoff);
                        sleep(sleepTime);
                        if (2 * backoff < MAX_BACKOFF_DELAY) {
                            backoff *= 2;
                        }
                    }
                } while (tryAgain);

                if (token != null) {
                    Log.i(TAG, "GCM Registration Token ready to be persisted in the third-party server: " + token);
                    //Send to third-party server
                    thirdPartyServer.sendRegistrationToServer(token);
                    // Subscribe to topic channels
                    //TODO

                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.
                    sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
                    //Store the actual token obtained from GCM server
                    sharedPreferences.edit().putString(Constants.GCM_TOKEN, token).apply();
                    // [END register_for_gcm]
                    // Notify UI that registration has completed, so the progress indicator can be hidden.
                    Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                } else {
                    //Do nothing
                    Log.d(TAG, "Failed to complete token request and persistence");
                    sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token request and persistence", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }

    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}