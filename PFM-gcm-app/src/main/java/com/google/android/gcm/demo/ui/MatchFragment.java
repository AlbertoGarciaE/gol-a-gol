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
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gcm.demo.R;
import com.google.android.gcm.demo.logic.MatchHelper;
import com.google.android.gcm.demo.model.Match;
import com.google.android.gcm.demo.service.LoggingService;

/**
 * This fragment shows a list of subscribed topics, allowing subscribing to new ones or
 * unsubscribing from the ones displayed.
 */
public class MatchFragment extends AbstractFragment implements View.OnClickListener, MainActivity.RefreshableFragment {

    private static final String ACTION_SHOW_LOG = "com.google.android.gcm.demo.ui.actionShowMatchLog";
    private static final String ACTION_HIDE_LOG = "com.google.android.gcm.demo.ui.actionHideMatchLog";

    private MatchHelper mMatchHelper;
    private LoggingService.Logger mLogger;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        mLogger = new LoggingService.Logger(getActivity());
        mMatchHelper = MatchHelper.getInstance();
        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_matches, container, false);
        TextView description = (TextView) view.findViewById(R.id.matches_description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setText(Html.fromHtml(getActivity().getString(R.string.matches_description)));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onClick(View v) {
        if (ACTION_SHOW_LOG.equals(v.getTag(R.id.tag_action))) {
            unsubscribe(v);
        } else if (ACTION_HIDE_LOG.equals(v.getTag(R.id.tag_action))) {
            subscribe(v);
        }
    }

    @Override
    public void refresh() {
        showMatches();
    }


    /**
     * Show the list of topics in the UI
     */
    private void showMatches() {
        int subscribedMatches = 0;
        //JSONArray result = this.mMatchHelper;
        float density = getActivity().getResources().getDisplayMetrics().density;
        LinearLayout LayoutMatchList = new LinearLayout(getActivity());
        LayoutMatchList.setOrientation(LinearLayout.VERTICAL);
//        if (result != null) {
//            List<Topic> topics = new ArrayList<>();
//            for (int i = 0; i < result.length(); i++) {
//                JSONObject j = result.optJSONObject(i);
//                try {
//                    topics.add(new Topic(j.getString("name"), j.getString("url")));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
        if (mMatchHelper != null) {
            for (Match match : mMatchHelper.getMatchList()) {
                if (!match.getMatchId().isEmpty()) {
                    subscribedMatches++;
                    LinearLayout row = (LinearLayout) getActivity().getLayoutInflater()
                            .inflate(R.layout.widget_icon_match_row, LayoutMatchList, false);
                    ImageView localIcon = (ImageView) row.findViewById(R.id.widget_local_icon);
                    ImageView awayIcon = (ImageView) row.findViewById(R.id.widget_away_icon);
                    TextView localLabel = (TextView) row.findViewById(R.id.widget_local_name);
                    TextView awayLabel = (TextView) row.findViewById(R.id.widget_away_name);
                    TextView scoreLabel = (TextView) row.findViewById(R.id.widget_score);
                    TextView statusLabel = (TextView) row.findViewById(R.id.widget_match_status);
                    Button button = (Button) row.findViewById(R.id.widget_itbr_button);
                    localIcon.setImageResource(R.drawable.bigtop_updates_grey600);
                    awayIcon.setImageResource(R.drawable.bigtop_updates_grey600);
                    localLabel.setText(match.getLocal());
                    awayLabel.setText(match.getAway());
                    String score = match.getLocalScore() + " - " + match.getAwayScore();
                    scoreLabel.setText(score);
                    statusLabel.setText(match.getStatus());
//TODO
                    if (true) {
                        button.setTag(R.id.tag_action, ACTION_SHOW_LOG);
                        button.setText(R.string.topics_unsubscribe);
                    } else {
                        button.setTag(R.id.tag_action, ACTION_HIDE_LOG);
                        button.setText(R.string.topics_subscribe);
                    }
                    button.setTag(R.id.tag_matchId, match.getMatchId());
                    button.setOnClickListener(this);
                    row.setPadding((int) (16 * density), 0, 0, 0);
                    LayoutMatchList.addView(row);
                }
            }


            if (subscribedMatches == 0)

            {
                TextView noTopics = new TextView(getActivity());
                noTopics.setText(R.string.topics_no_topic_subscribed);
                noTopics.setTypeface(null, Typeface.ITALIC);
                noTopics.setPadding((int) (16 * density), 0, 0, 0);
                LayoutMatchList.addView(noTopics);
            }

            FrameLayout topicsView = (FrameLayout) getActivity().findViewById(R.id.matches_list_wrapper);
            topicsView.removeAllViews();
            topicsView.addView(LayoutMatchList);
        }
    }

    private void updateTopic(View v) {


    }

    private void subscribe(View v) {

    }

    private void unsubscribe(View v) {

    }

}
