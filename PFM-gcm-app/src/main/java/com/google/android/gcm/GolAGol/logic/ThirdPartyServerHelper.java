package com.google.android.gcm.GolAGol.logic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.model.Topic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * Persist registration to the application server.
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
     * Remove registration from app server.
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
     * Get list of available subscription topics from the server in background
     * <p/>
     * If this task ends correctly, the list of topics is updated with the values obtained from the application server,
     * the status of the already subscribed topics is updated
     * and theUI is refreshed.
     */
    public void getTopics(Context context) {

        AsyncTask<Context, Void, Void> task = new AsyncTask<Context, Void, Void>() {
            String responseBody;
            public Context mContext = null;

            /**
             * Obtain the list of topics rom server
             * @param params
             * @return
             */
            @Override
            protected Void doInBackground(Context... params) {

                mContext = params[0];
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

            /**
             * When we have the list of topics from server as a JSONObject, we update the list of topics and the UI
             */
            @Override
            protected void onPostExecute(Void result) {
                try {
                    TopicHelper mTopics = TopicHelper.getInstance();
                    JSONArray resultArray = new JSONArray(responseBody);
                    List<Topic> mListTopics = new ArrayList<>();
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject j = resultArray.optJSONObject(i);
                        try {
                            mListTopics.add(new Topic(j.getString("name"), j.getString("url")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "New fresh topic list obtained from server");
                    mTopics.setListTopics(mListTopics);
                    //Update list with stored shared preferences for topics
                    Log.d(TAG, "Update new list of topics from server with stored preferences");
                    SharedPreferences appSharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(mContext);
                    String json = appSharedPrefs.getString(Constants.PREF_TOPIC_LIST, "");
                    if (!((json != null) && json.isEmpty())) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Topic>>() {
                        }.getType();
                        List<Topic> subscribedTopics = gson.fromJson(json, type);
                        Log.d(TAG, "Saved Topics subscribed " + subscribedTopics.size());
                        for (Topic topic : subscribedTopics) {
                            mTopics.updateTopicSubscriptionState(topic.getUrl(), topic.isSubscribed());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Refresh UI sending a LocalBroadcast intent
                Intent localIntent = new Intent(Constants.ACTION_REFRESH_UI);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);
            }

        };
        task.execute(context);
    }
}
