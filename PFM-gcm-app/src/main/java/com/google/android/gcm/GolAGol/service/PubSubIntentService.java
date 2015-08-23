package com.google.android.gcm.GolAGol.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.GolAGol.logic.MatchHelper;
import com.google.android.gcm.GolAGol.logic.TopicHelper;
import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.ui.AbstractFragment;
import com.google.android.gcm.GolAGol.ui.MatchFragment;
import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class PubSubIntentService extends IntentService {

    // IntentService can perform this actions
    private static final String PUBSUB_ACTION_SUBSCRIBE = "com.google.android.gcm.GolAGol.service.action.PUBSUB_ACTION_SUBSCRIBE";
    private static final String PUBSUB_ACTION_UNSUBSCRIBE = "com.google.android.gcm.GolAGol.service.action.PUBSUB_ACTION_UNSUBSCRIBE";

    //Parameter used in the intent service
    private static final String EXTRA_TOKEN = "com.google.android.gcm.GolAGol.service.extra.TOKEN";
    private static final String EXTRA_TOPIC = "com.google.android.gcm.GolAGol.service.extra.TOPIC";
    private static final String TAG = "PubSubIntentService";

    private TopicHelper mTopics;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void subscribeTopic(Context context, String topic, String token) {
        Intent intent = new Intent(context, PubSubIntentService.class);
        intent.setAction(PUBSUB_ACTION_SUBSCRIBE);
        intent.putExtra(EXTRA_TOPIC, topic);
        intent.putExtra(EXTRA_TOKEN, token);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void unsubscribeTopic(Context context, String topic, String token) {
        Intent intent = new Intent(context, PubSubIntentService.class);
        intent.setAction(PUBSUB_ACTION_UNSUBSCRIBE);
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
            if (PUBSUB_ACTION_SUBSCRIBE.equals(action)) {
                final String token = intent.getStringExtra(EXTRA_TOKEN);
                final String topic = intent.getStringExtra(EXTRA_TOPIC);
                handleSubscribeTopic(this, topic, token);
            } else if (PUBSUB_ACTION_UNSUBSCRIBE.equals(action)) {
                final String token = intent.getStringExtra(EXTRA_TOKEN);
                final String topic = intent.getStringExtra(EXTRA_TOPIC);
                handleUnsubscribeTopic(this, topic, token);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
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
            // Refres UI sending a LocalBroadcast intent
            Intent localIntent = new Intent(AbstractFragment.ACTION_REFRESH_UI);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } catch (IOException | IllegalArgumentException e) {
            Log.d(TAG, "topic subscription failed."
                    + "\nerror: " + e.getMessage()
                    + "\ngcmToken: " + token
                    + "\ntopic: " + topic);
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
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
            //TODO eliminar el match al que ya no estamos subscrito a sus notificaciones
            MatchHelper.getInstance().removeMatch(topic);
            //PreferenceManager.getDefaultSharedPreferences(this).edit().remove(MatchFragment.PREF_MATCHES_LIST).apply();
            // Refres UI sending a LocalBroadcast intent
            Intent localIntent = new Intent(AbstractFragment.ACTION_REFRESH_UI);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        } catch (IOException | IllegalArgumentException e) {
            Log.d(TAG, "topic unsubscription failed."
                    + "\nerror: " + e.getMessage()
                    + "\ngcmToken: " + token
                    + "\ntopic: " + topic);
        }
    }
}
