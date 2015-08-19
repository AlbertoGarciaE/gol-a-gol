package com.google.android.gcm.GolAGol.logic;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gcm.GolAGol.model.Constants.GET_TOPIC_LIST;
import static com.google.android.gcm.GolAGol.model.Constants.REGISTER_IN_SERVER;
import static com.google.android.gcm.GolAGol.model.Constants.UNREGISTER_IN_SERVER;
import static com.google.android.gcm.GolAGol.model.Constants.REST_PARAM_DEVICE_NAME;
import static com.google.android.gcm.GolAGol.model.Constants.REST_PARAM_REG_ID;
import static com.google.android.gcm.GolAGol.model.Constants.SERVER_URL_ROOT;

public class ThirdPartyServerHelper {
    private static final String TAG = "ThirdPartyServerHelper";

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     * @param name  The name associated to the new token.
     * @return True if operations ends correctly, False otherwise.
     */
    public boolean sendRegistrationToServer(String name, String token) {
        boolean result = false;
        Map<String, String> body = new HashMap<>();
        body.put(REST_PARAM_DEVICE_NAME, name);
        body.put(REST_PARAM_REG_ID, token);
        String encodedBody = REST_PARAM_DEVICE_NAME + "=" + name + "&" + REST_PARAM_REG_ID + "=" + token;
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHeader(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_FORM_ENCODED);
        try {
            httpRequest.doPost(SERVER_URL_ROOT + REGISTER_IN_SERVER, encodedBody);
            if (httpRequest.getResponseCode() != 200) {
                Log.e(TAG, "Invalid request."
                        + " status: " + httpRequest.getResponseCode()
                        + " response: " + httpRequest.getResponseBody());
                result = false;
            } else {
                JSONObject jsonResponse;
                jsonResponse = new JSONObject(httpRequest.getResponseBody());
                Log.d(TAG, "Send message:\n" + jsonResponse.toString(2));
                result = true;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException during post to server." + e.toString());
            e.printStackTrace();
            result = false;
        } catch (JSONException e) {
            Log.d(TAG, "Failed to parse server response:\n"
                    + httpRequest.getResponseBody());
            e.printStackTrace();
            result = false;
        }

        return result;

    }


    /**
     * Remove registration from third-party servers.
     * <p/>
     * Modify this method to erase the user's GCM registration token associated to any server-side account
     * maintained by your application.
     *
     * @param token The token to be erased.
     * @return True if operations ends correctly, False otherwise.
     */
    public boolean removeRegistrationFromServer(String token) {
        boolean result = false;
        Map<String, String> body = new HashMap<>();
        body.put(REST_PARAM_DEVICE_NAME, "name");
        body.put(REST_PARAM_REG_ID, token);
        String encodedBody = REST_PARAM_REG_ID + "=" + token;
        // Add custom implementation, as needed.
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHeader(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_FORM_ENCODED);
        try {
            httpRequest.doPost(SERVER_URL_ROOT + UNREGISTER_IN_SERVER, encodedBody);
            if (httpRequest.getResponseCode() != 200) {
                Log.e(TAG, "Invalid request."
                        + " status: " + httpRequest.getResponseCode()
                        + " response: " + httpRequest.getResponseBody());
                result = false;
            } else {
                JSONObject jsonResponse;
                jsonResponse = new JSONObject(httpRequest.getResponseBody());
                Log.d(TAG, "Send message:\n" + jsonResponse.toString(2));
                result = true;
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException during post to server." + e.toString());
            e.printStackTrace();
            result = false;
        } catch (JSONException e) {
            Log.d(TAG, "Failed to parse server response:\n"
                    + httpRequest.getResponseBody());
            e.printStackTrace();
            result = false;
        }

        return result;

    }


    /**
     * Get list of topics from the server
     *
     * @return JSONArray
     * Contain the list of topics retrieved from the server
     */
    public void getTopics(AsyncResponse delegate) {

        new AsyncTask<AsyncResponse, Void, Void>() {
            JSONArray topics = null;
            String responseBody;
            public AsyncResponse mDelegate = null;

            @Override
            protected Void doInBackground(AsyncResponse... params) {

                mDelegate = params[0];
                HttpRequest httpRequest = new HttpRequest();
                try {
                    httpRequest.doGet(SERVER_URL_ROOT + GET_TOPIC_LIST);
                    responseBody = httpRequest.getResponseBody();
                    //topics = new JSONArray(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //do stuff
                mDelegate.processFinish(responseBody);
            }

        }.execute(delegate);
    }


}
