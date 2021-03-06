package com.google.android.gcm.GolAGol.ui;

import android.content.Context;
import android.graphics.Typeface;
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

import com.google.android.gcm.GolAGol.R;
import com.google.android.gcm.GolAGol.logic.ThirdPartyServerHelper;
import com.google.android.gcm.GolAGol.logic.TopicHelper;
import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.model.Topic;
import com.google.android.gcm.GolAGol.service.PubSubIntentService;
import com.google.gson.Gson;

import java.util.List;

/**
 * This class displays a lis of subscription topics allowing
 * the user to subscribe or unsubscribe
 */
public class TopicsFragment extends AbstractFragment
        implements View.OnClickListener, MainActivity.RefreshableFragment {

    private static final String TAG = "TopicsFragment";

    private Context mContext;
    private TopicHelper mTopics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        mContext = getActivity().getApplicationContext();
        ThirdPartyServerHelper mThirdpartyServer = new ThirdPartyServerHelper();
        mTopics = TopicHelper.getInstance();
        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        TextView description = (TextView) view.findViewById(R.id.topics_description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setText(Html.fromHtml(getActivity().getString(R.string.topics_description)));

        Log.d(TAG, "Get topics from the server");
        mThirdpartyServer.getTopics(mContext);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveToPreferenceSubscribedTopics();
    }

    @Override
    public void onClick(View v) {
        if (Constants.ACTION_UNSUBSCRIBE.equals(v.getTag(R.id.tag_action))) {
            unsubscribe(v);
        } else if (Constants.ACTION_SUBSCRIBE.equals(v.getTag(R.id.tag_action))) {
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
        float density = getActivity().getResources().getDisplayMetrics().density;
        LinearLayout LayoutTopicList = new LinearLayout(getActivity());
        LayoutTopicList.setOrientation(LinearLayout.VERTICAL);
        if (mTopics != null) {
            for (Topic topic : mTopics.getListTopics()) {
                if (!topic.getUrl().isEmpty()) {
                    subscribedTopics++;
                    LinearLayout row = (LinearLayout) getActivity().getLayoutInflater()
                            .inflate(R.layout.widget_icon_text_button_row, LayoutTopicList, false);
                    ImageView icon = (ImageView) row.findViewById(R.id.widget_itbr_icon);
                    TextView label = (TextView) row.findViewById(R.id.widget_itbr_text);
                    Button button = (Button) row.findViewById(R.id.widget_itbr_button);
                    icon.setImageResource(R.drawable.bigtop_updates_grey600);
                    label.setText(topic.getUrl());

                    if (topic.isSubscribed()) {
                        button.setTag(R.id.tag_action, Constants.ACTION_UNSUBSCRIBE);
                        button.setText(R.string.topics_unsubscribe);
                    } else {
                        button.setTag(R.id.tag_action, Constants.ACTION_SUBSCRIBE);
                        button.setText(R.string.topics_subscribe);
                    }
                    button.setTag(R.id.tag_subscriptionState, topic.isSubscribed());
                    button.setTag(R.id.tag_topic, topic.getUrl());
                    button.setTag(R.id.tag_topic_name, topic.getName());
                    button.setOnClickListener(this);
                    row.setPadding((int) (16 * density), 0, 0, 0);
                    LayoutTopicList.addView(row);
                }
            }
            if (subscribedTopics == 0) {
                TextView noTopics = new TextView(getActivity());
                noTopics.setText(R.string.topics_no_topic_subscribed);
                noTopics.setTypeface(null, Typeface.ITALIC);
                noTopics.setPadding((int) (16 * density), 0, 0, 0);
                LayoutTopicList.addView(noTopics);
            }
            FrameLayout topicsView = (FrameLayout) getActivity().findViewById(R.id.topics_list_wrapper);
            topicsView.removeAllViews();
            topicsView.addView(LayoutTopicList);
        }
    }

    /**
     * Subscribe to the target topic
     *
     * @param v View that was clicked on to fire the call to the function
     */
    private void subscribe(View v) {
        String topic = (String) v.getTag(R.id.tag_topic);
        String gcmToken = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.GCM_TOKEN, "");
        if (gcmToken == null || gcmToken.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.topics_not_registered),
                    Toast.LENGTH_SHORT)
                    .show();
            Log.d(TAG, "gcmToken missing while subscribing to topic.");
        } else {
            PubSubIntentService.subscribeTopic(mContext, topic, gcmToken);
            Toast.makeText(getActivity(),
                    getString(R.string.topics_subscribing, topic),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Unsubcribe from the target topic
     *
     * @param v View that was clicked on to fire the call to the function
     */
    private void unsubscribe(View v) {
        String topic = (String) v.getTag(R.id.tag_topic);
        String gcmToken = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.GCM_TOKEN, "");
        if (gcmToken == null || gcmToken.isEmpty()) {
            Log.d(TAG, "gcmToken missing while unsubscribing from topic.");
        } else {
            PubSubIntentService.unsubscribeTopic(mContext, topic, gcmToken);
            Toast.makeText(getActivity(),
                    getString(R.string.topics_unsubscribing, topic),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Save to shared preferences the list of subscribed topics
     */
    private void saveToPreferenceSubscribedTopics() {
        List<Topic> subscribed = mTopics.getSubscribedTopics();
        Log.d(TAG, "Number of Topics subscribed that we are going to save " + subscribed.size());
        Gson gson = new Gson();
        String json = gson.toJson(subscribed);
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(Constants.PREF_TOPIC_LIST, json).apply();

    }

}
