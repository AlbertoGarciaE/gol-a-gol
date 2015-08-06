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
package com.google.android.gcm.demo.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.demo.R;
import com.google.android.gcm.demo.logic.AsyncResponse;
import com.google.android.gcm.demo.logic.HttpRequest;
import com.google.android.gcm.demo.logic.PubSubHelper;
import com.google.android.gcm.demo.logic.ThirdPartyServerHelper;
import com.google.android.gcm.demo.model.Constants;
import com.google.android.gcm.demo.model.Topic;
import com.google.android.gcm.demo.service.LoggingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gcm.demo.model.Constants.GET_TOPIC_LIST;
import static com.google.android.gcm.demo.model.Constants.SERVER_URL_ROOT;

/**
 * This fragment shows a list of subscribed topics, allowing subscribing to new ones or
 * unsubscribing from the ones displayed.
 */
public class TopicsFragment extends AbstractFragment
        implements View.OnClickListener, MainActivity.RefreshableFragment, AsyncResponse {

    private static final String ACTION_UNSUBSCRIBE = "actionUnsubscribe";
    private static final String ACTION_SUBSCRIBE = "actionSubscribe";

    private PubSubHelper mPubSubHelper;
    private ThirdPartyServerHelper mThirdpartyServer;
    private LoggingService.Logger mLogger;
    private Context mContext;

    private JSONArray mTopics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        mLogger = new LoggingService.Logger(getActivity());
        mPubSubHelper = new PubSubHelper(getActivity());
        mThirdpartyServer = new ThirdPartyServerHelper();
        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        TextView description = (TextView) view.findViewById(R.id.topics_description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setText(Html.fromHtml(getActivity().getString(R.string.topics_description)));
        mThirdpartyServer.getTopics(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onClick(View v) {
        if (ACTION_UNSUBSCRIBE.equals(v.getTag(R.id.tag_action))) {
            unsubscribe(v);
        } else if (ACTION_SUBSCRIBE.equals(v.getTag(R.id.tag_action))) {
            subscribe(v);
        }
    }

    @Override
    public void refresh() {
        showTopics();
    }


    /**
     * Show the list of topics in the UI
     */
    private void showTopics() {
        int subscribedTopics = 0;
        JSONArray result = this.mTopics;
        float density = getActivity().getResources().getDisplayMetrics().density;
        LinearLayout sendersList = new LinearLayout(getActivity());
        sendersList.setOrientation(LinearLayout.VERTICAL);
        if (result != null) {
            List<Topic> topics = new ArrayList<>();
            for (int i = 0; i < result.length(); i++) {
                JSONObject j = result.optJSONObject(i);
                try {
                    topics.add(new Topic(j.getString("name"), j.getString("url")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (Topic topic : topics) {
                if (!topic.getUrl().isEmpty()) {
                    subscribedTopics++;
                    LinearLayout row = (LinearLayout) getActivity().getLayoutInflater()
                            .inflate(R.layout.widget_icon_text_button_row, sendersList, false);
                    ImageView icon = (ImageView) row.findViewById(R.id.widget_itbr_icon);
                    TextView label = (TextView) row.findViewById(R.id.widget_itbr_text);
                    Button button = (Button) row.findViewById(R.id.widget_itbr_button);
                    icon.setImageResource(R.drawable.bigtop_updates_grey600);
                    label.setText(topic.getUrl());

                    if (topic.isSubscribed()) {
                        button.setTag(R.id.tag_action, ACTION_UNSUBSCRIBE);
                        button.setText(R.string.topics_unsubscribe);
                    } else {
                        button.setTag(R.id.tag_action, ACTION_SUBSCRIBE);
                        button.setText(R.string.topics_subscribe);
                    }
                    button.setTag(R.id.tag_subscriptionState, topic.isSubscribed());
                    button.setTag(R.id.tag_topic, topic.getUrl());
                    button.setOnClickListener(this);
                    row.setPadding((int) (16 * density), 0, 0, 0);
                    sendersList.addView(row);
                }
            }
        }
        if (subscribedTopics == 0) {
            TextView noTopics = new TextView(getActivity());
            noTopics.setText(R.string.topics_no_topic_subscribed);
            noTopics.setTypeface(null, Typeface.ITALIC);
            noTopics.setPadding((int) (16 * density), 0, 0, 0);
            sendersList.addView(noTopics);
        }

        FrameLayout topicsView = (FrameLayout) getActivity().findViewById(R.id.topics_list_wrapper);
        topicsView.removeAllViews();
        topicsView.addView(sendersList);
    }

    private void subscribe(View v) {
        String senderId = (String) getResources().getText(R.string.gcm_MyServer_SenderId);
        String topic = (String) v.getTag(R.id.tag_topic);
        String gcmToken = (senderId != null) ? PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.GCM_TOKEN, "") :
                null;
        if (gcmToken == null || gcmToken.isEmpty()) {
            mLogger.log(Log.ERROR, "gcmToken missing while subscribing to topic.");
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.topics_subscribing, topic),
                    Toast.LENGTH_SHORT)
                    .show();
            mPubSubHelper.subscribeTopic(senderId, gcmToken, topic, null);
        }
    }

    private void unsubscribe(View v) {
        String senderId = (String) getResources().getText(R.string.gcm_MyServer_SenderId);
        String topic = (String) v.getTag(R.id.tag_topic);
        String gcmToken = (senderId != null) ? PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.GCM_TOKEN, "") :
                null;
        if (gcmToken == null || gcmToken.isEmpty()) {
            mLogger.log(Log.ERROR, "gcmToken missing while unsubscribing from topic.");
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.topics_unsubscribing, topic),
                    Toast.LENGTH_SHORT)
                    .show();
            mPubSubHelper.unsubscribeTopic(senderId, gcmToken, topic);
        }
    }

    @Override
    public void processFinish(String output) {
        try {
            this.mTopics = new JSONArray(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh();

    }
}
