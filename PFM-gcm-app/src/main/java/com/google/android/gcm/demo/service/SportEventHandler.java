package com.google.android.gcm.demo.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gcm.demo.logic.MatchHelper;
import com.google.android.gcm.demo.model.Match;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class SportEventHandler extends IntentService {

    private static final String ACTION_SCORE = "com.google.android.gcm.demo.service.action.SCORE";
    private static final String ACTION_REGULAR_ACTION = "com.google.android.gcm.demo.service.action.REGULAR_ACTION";

    public static final String EXTRA_MATCHID = "com.google.android.gcm.demo.service.extra.matchId";
    public static final String EXTRA_LOCAL = "com.google.android.gcm.demo.service.extra.local";
    public static final String EXTRA_AWAY = "com.google.android.gcm.demo.service.extra.away";
    public static final String EXTRA_LOCAL_SCORE = "com.google.android.gcm.demo.service.extra.localScore";
    public static final String EXTRA_AWAY_SCORE = "com.google.android.gcm.demo.service.extra.awayScore";
    public static final String EXTRA_STATUS = "com.google.android.gcm.demo.service.extra.status";
    public static final String EXTRA_COMMENT = "com.google.android.gcm.demo.service.extra.comment";
    public static final String ACTION_REFRESH_UI = "com.google.android.gcm.demo.service.extra.refreshUi";
    private MatchHelper matchHlpr = MatchHelper.getInstance();

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionScore(Context context, String matchId, String local, String away,
                                      String localScore, String awayScore, String status, String comment) {
        Intent intent = new Intent(context, SportEventHandler.class);
        intent.setAction(ACTION_SCORE);
        intent.putExtra(EXTRA_MATCHID, matchId);
        intent.putExtra(EXTRA_LOCAL, local);
        intent.putExtra(EXTRA_AWAY, away);
        intent.putExtra(EXTRA_LOCAL_SCORE, localScore);
        intent.putExtra(EXTRA_AWAY_SCORE, awayScore);
        intent.putExtra(EXTRA_STATUS, status);
        intent.putExtra(EXTRA_COMMENT, comment);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionRegularAction(Context context, String matchId, String local,
                                      String away, String localScore, String awayScore, String status, String comment) {
        Intent intent = new Intent(context, SportEventHandler.class);
        intent.setAction(ACTION_REGULAR_ACTION);
        intent.putExtra(EXTRA_MATCHID, matchId);
        intent.putExtra(EXTRA_LOCAL, local);
        intent.putExtra(EXTRA_AWAY, away);
        intent.putExtra(EXTRA_LOCAL_SCORE, localScore);
        intent.putExtra(EXTRA_AWAY_SCORE, awayScore);
        intent.putExtra(EXTRA_STATUS, status);
        intent.putExtra(EXTRA_COMMENT, comment);
        context.startService(intent);
    }

    public SportEventHandler() {
        super("SportEventHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bundle bundle = new Bundle();
            final String action = intent.getAction();
            if (ACTION_SCORE.equals(action)) {
                bundle.putString(EXTRA_MATCHID, intent.getStringExtra(EXTRA_MATCHID));
                bundle.putString(EXTRA_LOCAL, intent.getStringExtra(EXTRA_LOCAL));
                bundle.putString(EXTRA_AWAY, intent.getStringExtra(EXTRA_AWAY));
                bundle.putString(EXTRA_LOCAL_SCORE, intent.getStringExtra(EXTRA_LOCAL_SCORE));
                bundle.putString(EXTRA_AWAY_SCORE, intent.getStringExtra(EXTRA_AWAY_SCORE));
                bundle.putString(EXTRA_STATUS, intent.getStringExtra(EXTRA_STATUS));
                bundle.putString(EXTRA_COMMENT, intent.getStringExtra(EXTRA_COMMENT));
                handleActionScore(bundle);
            } else if (ACTION_REGULAR_ACTION.equals(action)) {
                bundle.putString(EXTRA_MATCHID, intent.getStringExtra(EXTRA_MATCHID));
                bundle.putString(EXTRA_LOCAL, intent.getStringExtra(EXTRA_LOCAL));
                bundle.putString(EXTRA_AWAY, intent.getStringExtra(EXTRA_AWAY));
                bundle.putString(EXTRA_LOCAL_SCORE, intent.getStringExtra(EXTRA_LOCAL_SCORE));
                bundle.putString(EXTRA_AWAY_SCORE, intent.getStringExtra(EXTRA_AWAY_SCORE));
                bundle.putString(EXTRA_STATUS, intent.getStringExtra(EXTRA_STATUS));
                bundle.putString(EXTRA_COMMENT, intent.getStringExtra(EXTRA_COMMENT));
                handleActionRegularAction(bundle);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionScore(Bundle data) {
        String matchId = data.getString(EXTRA_MATCHID);
        Match auxMatch = matchHlpr.getMatch(matchId);
        if (auxMatch != null) {
            auxMatch.setLocal(data.getString(EXTRA_LOCAL));
            auxMatch.setAway(data.getString(EXTRA_AWAY));
            auxMatch.setLocalScore(Integer.getInteger(data.getString(EXTRA_LOCAL_SCORE)));
            auxMatch.setAwayScore(Integer.getInteger(data.getString(EXTRA_AWAY_SCORE)));
            auxMatch.setStatus(data.getString(EXTRA_STATUS));
            auxMatch.appendLine(data.getString(EXTRA_COMMENT));
        } else {
            auxMatch = new Match(data.getString(EXTRA_MATCHID));
            auxMatch.setLocal(data.getString(EXTRA_LOCAL));
            auxMatch.setAway(data.getString(EXTRA_AWAY));
            auxMatch.setLocalScore(Integer.getInteger(data.getString(EXTRA_LOCAL_SCORE)));
            auxMatch.setAwayScore(Integer.getInteger(data.getString(EXTRA_AWAY_SCORE)));
            auxMatch.setStatus(data.getString(EXTRA_STATUS));
            auxMatch.appendLine(data.getString(EXTRA_COMMENT));
            matchHlpr.addMatch(auxMatch);
        }
        // Forward the log to LocalBroadcast subscribers (i.e. UI)
        Intent localIntent = new Intent(ACTION_REFRESH_UI);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRegularAction(Bundle data) {
        String matchId = data.getString(EXTRA_MATCHID);
        Match auxMatch = matchHlpr.getMatch(matchId);
        if (auxMatch != null) {
            auxMatch.setLocal(data.getString(EXTRA_LOCAL));
            auxMatch.setAway(data.getString(EXTRA_AWAY));
            auxMatch.setLocalScore(Integer.getInteger(data.getString(EXTRA_LOCAL_SCORE)));
            auxMatch.setAwayScore(Integer.getInteger(data.getString(EXTRA_AWAY_SCORE)));
            auxMatch.setStatus(data.getString(EXTRA_STATUS));
            auxMatch.appendLine(data.getString(EXTRA_COMMENT));
        } else {
            auxMatch = new Match(data.getString(EXTRA_MATCHID));
            auxMatch.setLocal(data.getString(EXTRA_LOCAL));
            auxMatch.setAway(data.getString(EXTRA_AWAY));
            auxMatch.setLocalScore(Integer.getInteger(data.getString(EXTRA_LOCAL_SCORE)));
            auxMatch.setAwayScore(Integer.getInteger(data.getString(EXTRA_AWAY_SCORE)));
            auxMatch.setStatus(data.getString(EXTRA_STATUS));
            auxMatch.appendLine(data.getString(EXTRA_COMMENT));
            matchHlpr.addMatch(auxMatch);
        }
        // Forward the log to LocalBroadcast subscribers (i.e. UI)
        Intent localIntent = new Intent(ACTION_REFRESH_UI);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
