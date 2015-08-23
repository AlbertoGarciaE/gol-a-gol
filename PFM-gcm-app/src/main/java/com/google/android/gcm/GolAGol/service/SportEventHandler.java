package com.google.android.gcm.GolAGol.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.GolAGol.logic.MatchHelper;
import com.google.android.gcm.GolAGol.model.Match;
import com.google.android.gcm.GolAGol.ui.AbstractFragment;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class SportEventHandler extends IntentService {

    private static final String ACTION_SCORE = "com.google.android.gcm.GolAGol.service.action.SCORE";
    private static final String ACTION_REGULAR_ACTION = "com.google.android.gcm.GolAGol.service.action.REGULAR_ACTION";

    public static final String EXTRA_MATCHID = "matchId";
    public static final String EXTRA_LOCAL = "local";
    public static final String EXTRA_AWAY = "away";
    public static final String EXTRA_LOCAL_SCORE = "localScore";
    public static final String EXTRA_AWAY_SCORE = "awayScore";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_COMMENT = "comment";
    private static final String TAG = "SportEventHandler";
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
        super(TAG);
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
        if (matchId != null && !matchId.isEmpty()) {
            Match auxMatch = matchHlpr.getMatch(matchId);
            if (auxMatch != null) {
                Log.d(TAG, "Match already exist, update it with notification match information");
                String newLocal = data.getString(EXTRA_LOCAL);
                if (!newLocal.isEmpty() && newLocal != null) {
                    auxMatch.setLocal(newLocal);
                }
                String newAway = data.getString(EXTRA_AWAY);
                if (!newAway.isEmpty() && newAway != null) {
                    auxMatch.setAway(newAway);
                }
                String newLocalScore = data.getString(EXTRA_LOCAL_SCORE);
                if (!newLocalScore.isEmpty() && newLocalScore != null) {
                    auxMatch.setLocalScore(Integer.parseInt(newLocalScore));
                }
                String newAwayScore = data.getString(EXTRA_AWAY_SCORE);
                if (!newAwayScore.isEmpty() && newAwayScore != null) {
                    auxMatch.setAwayScore(Integer.parseInt(newAwayScore));
                }
                String newStatus = data.getString(EXTRA_STATUS);
                if (!newStatus.isEmpty() && newStatus != null) {
                    auxMatch.setStatus(newStatus);
                }
                String newLine = data.getString(EXTRA_COMMENT);
                if (!newLine.isEmpty() && newLine != null) {
                    auxMatch.appendLine(newLine);
                }
            } else {
                Log.d(TAG, "Creating new match with notification match information");
                auxMatch = new Match(data.getString(EXTRA_MATCHID));
                auxMatch.setLocal(data.getString(EXTRA_LOCAL));
                auxMatch.setAway(data.getString(EXTRA_AWAY));
                String aux = data.getString(EXTRA_LOCAL_SCORE);
                auxMatch.setLocalScore(Integer.parseInt(aux));
                auxMatch.setAwayScore(Integer.parseInt(data.getString(EXTRA_AWAY_SCORE)));
                auxMatch.setStatus(data.getString(EXTRA_STATUS));
                auxMatch.appendLine(data.getString(EXTRA_COMMENT));
                matchHlpr.addMatch(auxMatch);
            }
        } else {
            Log.d(TAG, "Notification contains no match information, drop it");
        }
        // Refres UI sending a LocalBroadcast intent
        Intent localIntent = new Intent(AbstractFragment.ACTION_REFRESH_UI);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRegularAction(Bundle data) {
        String matchId = data.getString(EXTRA_MATCHID);
        if (matchId != null && !matchId.isEmpty()) {
            Match auxMatch = matchHlpr.getMatch(matchId);
            if (auxMatch != null) {
                Log.d(TAG, "Match already exist, update it with notification match information");
                String newLocal = data.getString(EXTRA_LOCAL);
                if (!newLocal.isEmpty() && newLocal != null) {
                    auxMatch.setLocal(newLocal);
                }
                String newAway = data.getString(EXTRA_AWAY);
                if (!newAway.isEmpty() && newAway != null) {
                    auxMatch.setAway(newAway);
                }
                String newLocalScore = data.getString(EXTRA_LOCAL_SCORE);
                if (!newLocalScore.isEmpty() && newLocalScore != null) {
                    auxMatch.setLocalScore(Integer.parseInt(newLocalScore));
                }
                String newAwayScore = data.getString(EXTRA_AWAY_SCORE);
                if (!newAwayScore.isEmpty() && newAwayScore != null) {
                    auxMatch.setAwayScore(Integer.parseInt(newAwayScore));
                }
                String newStatus = data.getString(EXTRA_STATUS);
                if (!newStatus.isEmpty() && newStatus != null) {
                    auxMatch.setStatus(newStatus);
                }
                String newLine = data.getString(EXTRA_COMMENT);
                if (!newLine.isEmpty() && newLine != null) {
                    auxMatch.appendLine(newLine);
                }
            } else {
                Log.d(TAG, "Creating new match with notification match information");
                auxMatch = new Match(data.getString(EXTRA_MATCHID));
                auxMatch.setLocal(data.getString(EXTRA_LOCAL));
                auxMatch.setAway(data.getString(EXTRA_AWAY));
                String aux = data.getString(EXTRA_LOCAL_SCORE);
                auxMatch.setLocalScore(Integer.parseInt(aux));
                auxMatch.setAwayScore(Integer.parseInt(data.getString(EXTRA_AWAY_SCORE)));
                auxMatch.setStatus(data.getString(EXTRA_STATUS));
                auxMatch.appendLine(data.getString(EXTRA_COMMENT));
                matchHlpr.addMatch(auxMatch);
            }
        } else {
            Log.d(TAG, "Notification contains no match information, drop it");
        }
        // Refres UI sending a LocalBroadcast intent
        Intent localIntent = new Intent(AbstractFragment.ACTION_REFRESH_UI);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }
}
