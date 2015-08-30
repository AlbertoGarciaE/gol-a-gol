package com.google.android.gcm.GolAGol.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gcm.GolAGol.logic.MatchHelper;
import com.google.android.gcm.GolAGol.model.Constants;
import com.google.android.gcm.GolAGol.model.Match;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class SportEventHandler extends IntentService {

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
     * Starts this service to perform action Score with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionScore(Context context, String matchId, String local, String away,
                                        String localScore, String awayScore, String status, String comment) {
        Intent intent = new Intent(context, SportEventHandler.class);
        intent.setAction(Constants.ACTION_SCORE);
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
     * Starts this service to perform action Regular Action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionRegularAction(Context context, String matchId, String local,
                                                String away, String localScore, String awayScore, String status, String comment) {
        Intent intent = new Intent(context, SportEventHandler.class);
        intent.setAction(Constants.ACTION_REGULAR_ACTION);
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
            if (Constants.ACTION_SCORE.equals(action)) {
                bundle.putString(EXTRA_MATCHID, intent.getStringExtra(EXTRA_MATCHID));
                bundle.putString(EXTRA_LOCAL, intent.getStringExtra(EXTRA_LOCAL));
                bundle.putString(EXTRA_AWAY, intent.getStringExtra(EXTRA_AWAY));
                bundle.putString(EXTRA_LOCAL_SCORE, intent.getStringExtra(EXTRA_LOCAL_SCORE));
                bundle.putString(EXTRA_AWAY_SCORE, intent.getStringExtra(EXTRA_AWAY_SCORE));
                bundle.putString(EXTRA_STATUS, intent.getStringExtra(EXTRA_STATUS));
                bundle.putString(EXTRA_COMMENT, intent.getStringExtra(EXTRA_COMMENT));
                handleActionScore(bundle);
            } else if (Constants.ACTION_REGULAR_ACTION.equals(action)) {
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
     * Handle action Score in the provided background thread with the provided
     * parameters.
     */
    private void handleActionScore(Bundle data) {
        String matchId = data.getString(EXTRA_MATCHID);
        if (matchId != null && !matchId.isEmpty()) {
            Match auxMatch = matchHlpr.getMatch(matchId);
            if (auxMatch != null) {
                //Update Match
                Log.d(TAG, "Match already exist, update it with notification match information");
                String newLocal = data.getString(EXTRA_LOCAL, "");
                if (!newLocal.isEmpty()) {
                    auxMatch.setLocal(newLocal);
                }
                String newAway = data.getString(EXTRA_AWAY, "");
                if (!newAway.isEmpty()) {
                    auxMatch.setAway(newAway);
                }
                String newLocalScore = data.getString(EXTRA_LOCAL_SCORE, "");
                if (!newLocalScore.isEmpty()) {
                    auxMatch.setLocalScore(Integer.parseInt(newLocalScore));
                }
                String newAwayScore = data.getString(EXTRA_AWAY_SCORE, "");
                if (!newAwayScore.isEmpty()) {
                    auxMatch.setAwayScore(Integer.parseInt(newAwayScore));
                }
                String newStatus = data.getString(EXTRA_STATUS, "");
                if (!newStatus.isEmpty()) {
                    auxMatch.setStatus(newStatus);
                }
                String newLine = data.getString(EXTRA_COMMENT, "");
                if (!newLine.isEmpty()) {
                    auxMatch.appendLine(newLine);
                }
            } else {
                //New Match
                Log.d(TAG, "Creating new match with notification match information");
                auxMatch = new Match(data.getString(EXTRA_MATCHID));
                String newLocal = data.getString(EXTRA_LOCAL, "Local");
                if (!newLocal.isEmpty()) {
                    auxMatch.setLocal(newLocal);
                }
                String newAway = data.getString(EXTRA_AWAY, "Visitante");
                if (!newAway.isEmpty()) {
                    auxMatch.setAway(newAway);
                }
                String newLocalScore = data.getString(EXTRA_LOCAL_SCORE, "0");
                if (!newLocalScore.isEmpty()) {
                    auxMatch.setLocalScore(Integer.parseInt(newLocalScore));
                }
                String newAwayScore = data.getString(EXTRA_AWAY_SCORE, "0");
                if (!newAwayScore.isEmpty()) {
                    auxMatch.setAwayScore(Integer.parseInt(newAwayScore));
                }
                String newStatus = data.getString(EXTRA_STATUS, "Desc");
                if (!newStatus.isEmpty()) {
                    auxMatch.setStatus(newStatus);
                }
                String newLine = data.getString(EXTRA_COMMENT, "");
                if (!newLine.isEmpty()) {
                    auxMatch.appendLine(newLine);
                }
                matchHlpr.addMatch(auxMatch);
            }
        } else {
            Log.d(TAG, "Notification contains no match information, drop it");
        }
        // Refresh UI sending a LocalBroadcast intent
        Intent localIntent = new Intent(Constants.ACTION_REFRESH_UI);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }

    /**
     * Handle action Regular Action in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRegularAction(Bundle data) {
        String matchId = data.getString(EXTRA_MATCHID);
        if (matchId != null && !matchId.isEmpty()) {
            Match auxMatch = matchHlpr.getMatch(matchId);
            if (auxMatch != null) {
                //Update Match
                Log.d(TAG, "Match already exist, update it with notification match information");
                String newLocal = data.getString(EXTRA_LOCAL, "");
                if (!newLocal.isEmpty()) {
                    auxMatch.setLocal(newLocal);
                }
                String newAway = data.getString(EXTRA_AWAY, "");
                if (!newAway.isEmpty()) {
                    auxMatch.setAway(newAway);
                }
                String newLocalScore = data.getString(EXTRA_LOCAL_SCORE, "");
                if (!newLocalScore.isEmpty()) {
                    auxMatch.setLocalScore(Integer.parseInt(newLocalScore));
                }
                String newAwayScore = data.getString(EXTRA_AWAY_SCORE, "");
                if (!newAwayScore.isEmpty()) {
                    auxMatch.setAwayScore(Integer.parseInt(newAwayScore));
                }
                String newStatus = data.getString(EXTRA_STATUS, "");
                if (!newStatus.isEmpty()) {
                    auxMatch.setStatus(newStatus);
                }
                String newLine = data.getString(EXTRA_COMMENT, "");
                if (!newLine.isEmpty()) {
                    auxMatch.appendLine(newLine);
                }
            } else {
                //New Match
                Log.d(TAG, "Creating new match with notification match information");
                auxMatch = new Match(data.getString(EXTRA_MATCHID));
                String newLocal = data.getString(EXTRA_LOCAL, "Local");
                if (!newLocal.isEmpty()) {
                    auxMatch.setLocal(newLocal);
                }
                String newAway = data.getString(EXTRA_AWAY, "Visitante");
                if (!newAway.isEmpty()) {
                    auxMatch.setAway(newAway);
                }
                String newLocalScore = data.getString(EXTRA_LOCAL_SCORE, "0");
                if (!newLocalScore.isEmpty()) {
                    auxMatch.setLocalScore(Integer.parseInt(newLocalScore));
                }
                String newAwayScore = data.getString(EXTRA_AWAY_SCORE, "0");
                if (!newAwayScore.isEmpty()) {
                    auxMatch.setAwayScore(Integer.parseInt(newAwayScore));
                }
                String newStatus = data.getString(EXTRA_STATUS, "Desc");
                if (!newStatus.isEmpty()) {
                    auxMatch.setStatus(newStatus);
                }
                String newLine = data.getString(EXTRA_COMMENT, "");
                if (!newLine.isEmpty()) {
                    auxMatch.appendLine(newLine);
                }
                matchHlpr.addMatch(auxMatch);
            }
        } else {
            Log.d(TAG, "Notification contains no match information, drop it");
        }
        // Refresh UI sending a LocalBroadcast intent
        Intent localIntent = new Intent(Constants.ACTION_REFRESH_UI);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }
}
