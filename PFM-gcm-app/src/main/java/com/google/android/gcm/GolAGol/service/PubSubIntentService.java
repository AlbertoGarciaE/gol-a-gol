package com.google.android.gcm.GolAGol.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.GolAGol.logic.MatchHelper;
import com.google.android.gcm.GolAGol.logic.TopicHelper;
import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class PubSubIntentService extends IntentService {

    //Parameter used in the intent service
    private static final String EXTRA_TOKEN = "token";
    private static final String EXTRA_TOPIC = "topic";
    private static final String TAG = "PubSubIntentService";

    private TopicHelper mTopics;

    /**
     * Starts this service to perform action Subscribe Topic with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void subscribeTopic(Context context, String topic, String token) {
        Intent intent = new Intent(context, PubSubIntentService.class);
        intent.setAction(Constants.ACTION_PUBSUB_SUBSCRIBE);
        intent.putExtra(EXTRA_TOPIC, topic);
        intent.putExtra(EXTRA_TOKEN, token);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Unsubscribe Topic with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void unsubscribeTopic(Context context, String topic, String token) {
        Intent intent = new Intent(context, PubSubIntentService.class);
        intent.setAction(Constants.ACTION_PUBSUB_UNSUBSCRIBE);
        intent.putExtra(EXTRA_TOPIC, topic);
        intent.putExtra(EXTRA_TOKEN, token);
        context.startService(intent);
    }

    public PubSubIntentService() {
        super(TAG);
        mTopics = TopicHelper.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_PUBSUB_SUBSCRIBE.equals(action)) {
                final String token = intent.getStringExtra(EXTRA_TOKEN);
                final String topic = intent.getStringExtra(EXTRA_TOPIC);
                handleSubscribeTopic(this, topic, token);
            } else if (Constants.ACTION_PUBSUB_UNSUBSCRIBE.equals(action)) {
                final String token = intent.getStringExtra(EXTRA_TOKEN);
                final String topic = intent.getStringExtra(EXTRA_TOPIC);
                handleUnsubscribeTopic(this, topic, token);
            }
        }
    }

    /**
     * Handle action Subscribe Topic in the provided background thread with the provided
     * parameters.
     */
    private void handleSubscribeTopic(Context context, String topic, String token) {
        try {
            Bundle extras = new Bundle();
            GcmPubSub.getInstance(context).subscribe(token, topic, extras);
            Log.d(TAG, "topic subscription succeeded."
                    + "\ngcmToken: " + token
                    + "\ntopic: " + topic
                    + "\nextras: " + extras);
            // Update topic list
            mTopics.updateTopicSubscriptionState(topic, true);
            // Refresh UI sending a LocalBroadcast intent
            Intent localIntent = new Intent(Constants.ACTION_REFRESH_UI);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } catch (IOException | IllegalArgumentException e) {
            Log.d(TAG, "topic subscription failed."
                    + "\nerror: " + e.getMessage()
                    + "\ngcmToken: " + token
                    + "\ntopic: " + topic);
        }
    }

    /**
     * Handle action Unsubsidised Topic in the provided background thread with the provided
     * parameters.
     */
    private void handleUnsubscribeTopic(Context context, String topic, String token) {
        try {
            GcmPubSub.getInstance(context).unsubscribe(token, topic);
            Log.d(TAG, "topic unsubscription succeeded."
                    + "\ngcmToken: " + token
                    + "\ntopic: " + topic);
            // Update topic list
            mTopics.updateTopicSubscriptionState(topic, false);
            MatchHelper.getInstance().removeMatch(topic);
            // Refresh UI sending a LocalBroadcast intent
            Intent localIntent = new Intent(Constants.ACTION_REFRESH_UI);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } catch (IOException | IllegalArgumentException e) {
            Log.d(TAG, "topic unsubscription failed."
                    + "\nerror: " + e.getMessage()
                    + "\ngcmToken: " + token
                    + "\ntopic: " + topic);
        }
    }
}
