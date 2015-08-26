package com.google.android.gcm.GolAGol.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.android.gcm.GolAGol.R;
import com.google.android.gcm.GolAGol.logic.MatchHelper;
import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.model.Match;
import com.google.android.gcm.GolAGol.service.SportEventHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * This fragment shows a list of subscribed topics, allowing subscribing to new ones or
 * unsubscribing from the ones displayed.
 */
public class MatchFragment extends AbstractFragment implements View.OnClickListener, MainActivity.RefreshableFragment {

    private MatchHelper mMatchHelper;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        mMatchHelper = MatchHelper.getInstance();
        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_matches, container, false);
        TextView description = (TextView) view.findViewById(R.id.matches_description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setText(Html.fromHtml(getActivity().getString(R.string.matches_description)));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (Constants.ACTION_SHOW_LOG.equals(v.getTag(R.id.tag_action))) {
            showMatchLog(v);
        }
    }

    @Override
    public void refresh() {
        showMatches();
    }


    /**
     * Show the list of matches in the UI
     */
    private void showMatches() {
        int subscribedMatches = 0;
        float density = getActivity().getResources().getDisplayMetrics().density;
        LinearLayout LayoutMatchList = new LinearLayout(getActivity());
        LayoutMatchList.setOrientation(LinearLayout.VERTICAL);
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
                    localIcon.setImageResource(getResources().getIdentifier(match.getLocal(), null, null));
                    awayIcon.setImageResource(getResources().getIdentifier(match.getAway(), null, null));
                    localLabel.setText(match.getLocal());
                    awayLabel.setText(match.getAway());
                    String score = match.getLocalScore() + " - " + match.getAwayScore();
                    scoreLabel.setText(score);
                    statusLabel.setText(match.getStatus());
                    button.setTag(R.id.tag_action, Constants.ACTION_SHOW_LOG);
                    button.setTag(R.id.tag_matchId, match.getMatchId());
                    button.setText(R.string.matches_show_log);
                    button.setOnClickListener(this);
                    row.setPadding((int) (16 * density), 0, 0, 0);
                    LayoutMatchList.addView(row);
                }
            }


            if (subscribedMatches == 0) {
                TextView noTopics = new TextView(getActivity());
                noTopics.setText(R.string.topics_no_topic_subscribed);
                noTopics.setTypeface(null, Typeface.ITALIC);
                noTopics.setPadding((int) (16 * density), 0, 0, 0);
                LayoutMatchList.addView(noTopics);
            }

            FrameLayout matchesView = (FrameLayout) getActivity().findViewById(R.id.matches_list_wrapper);
            matchesView.removeAllViews();
            matchesView.addView(LayoutMatchList);
        }
    }

    /**
     * Show the actions of the selected match in the log view
     *
     * @param v View that was clicked on to fire the call to the function
     */
    private void showMatchLog(View v) {
        // Forward the log to LocalBroadcast subscribers (i.e. UI)
        Intent localIntent = new Intent(Constants.ACTION_SHOW_LOG);
        String matchId = (String) v.getTag(R.id.tag_matchId);
        Match auxMatch = mMatchHelper.getMatch(matchId);
        String comments = "";
        if (auxMatch != null) {
            comments = auxMatch.getLog();
        }
        localIntent.putExtra(SportEventHandler.EXTRA_COMMENT, comments);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);
    }

    private void saveToPreferenceMatches() {
        List<Match> matches = mMatchHelper.getMatchList();
        Gson gson = new Gson();
        String json = gson.toJson(matches);
        getDefaultSharedPreferences(mContext).edit().putString(Constants.PREF_MATCHES_LIST, json).apply();
    }

    private void getMatchesListFromPreferences() {
        SharedPreferences appSharedPrefs =
                getDefaultSharedPreferences(mContext);
        String json = appSharedPrefs.getString(Constants.PREF_MATCHES_LIST, "");
        if (!(json != null && json.isEmpty())) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Match>>() {
            }.getType();
            List<Match> matches = gson.fromJson(json, type);
            mMatchHelper.initMatchList(matches);
        }
    }

}
