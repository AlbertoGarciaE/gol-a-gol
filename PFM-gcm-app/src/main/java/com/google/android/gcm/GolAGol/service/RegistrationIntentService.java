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

package com.google.android.gcm.GolAGol.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.GolAGol.logic.ThirdPartyServerHelper;
import com.google.android.gcm.GolAGol.R;
import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.ui.AbstractFragment;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.Random;

/**
 * This class used to register and unregister the app for GCM.
 * Registration involves getting the app's instance id and using it to request a token with
 * the scope {@link GoogleCloudMessaging.INSTANCE_ID_SCOPE} and the audience set to the project's
 * id.
 */
public class RegistrationIntentService extends IntentService {

    //Actions for the intent service
    public static final String REGISTRATION_I_S_ACTION_GET_TOKEN = "getToken";
    public static final String REGISTRATION_I_S_ACTION_ERASE_TOKEN = "eraseToken";

    // Initial delay before first retry, without jitter.
    private static final int BACKOFF_INITIAL_DELAY = 1000;
    //Maximum delay before a retry.
    private static final int MAX_BACKOFF_DELAY = 1024000;
    private final Random random;
    private static final String TAG = "RegistrationIntentServi";
    private ThirdPartyServerHelper thirdPartyServer;

    public RegistrationIntentService() {
        super(TAG);
        thirdPartyServer = new ThirdPartyServerHelper();
        random = new Random();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean alreadyRegistered = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
        if (REGISTRATION_I_S_ACTION_GET_TOKEN.equals(intent.getAction()) && !alreadyRegistered) {
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
                    String deviceName = intent.getStringExtra(Constants.REST_PARAM_DEVICE_NAME);
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
                        // Send to third-party server,
                        // set a boolean value in shared preferences to know that the token was already sent to the server
                        // and store the token also in preferences
                        if (thirdPartyServer.sendRegistrationToServer(deviceName, token)) {
                            sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
                            sharedPreferences.edit().putString(Constants.GCM_TOKEN, token).apply();
                            Log.d(TAG, "Success: token obtained and persisted in server");
                        }
                        // [END register_for_gcm]
                    } else {
                        //No token, Do nothing
                        sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
                        Log.d(TAG, "Fail: Error during token request and persistence");

                    }
                }
            } catch (Exception e) {
                // If an exception happens while fetching the new token or updating our registration data
                // on a third-party server, this ensures that we'll attempt the update at a later time.
                Log.d(TAG, "Failed to complete token request", e);
                sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
            }
        } else if (REGISTRATION_I_S_ACTION_ERASE_TOKEN.equals(intent.getAction()) && alreadyRegistered) {
            String senderId = this.getString(R.string.gcm_MyServer_SenderId);
            String token = sharedPreferences.getString(Constants.GCM_TOKEN, "");
            try {
                InstanceID.getInstance(this).deleteToken(senderId,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                //Delete from server
                if (thirdPartyServer.removeRegistrationFromServer(token)) {
                    sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
                    sharedPreferences.edit().putString(Constants.GCM_TOKEN, "").apply();
                    Log.d(TAG, "delete token succeeded." +
                            "\nsenderId: " + senderId);
                }

            } catch (final Exception e) {
                Log.d(TAG, "remove token failed." +
                        "\nsenderId: " + senderId + "\nerror: " + e.getMessage());
                sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
            }
        }

        // Refres UI sending a LocalBroadcast intent
        Intent localIntent = new Intent(AbstractFragment.ACTION_REFRESH_UI);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}