package com.google.android.gcm.GolAGol.service;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;


/**
 * Service used for receiving GCM messages.
 * When a message is received this service will create/Update a Match to show the information.
 */
public class GcmService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotificationMatch(data);
    }

    @Override
    public void onDeletedMessages() {

    }

    @Override
    public void onMessageSent(String msgId) {

    }

    @Override
    public void onSendError(String msgId, String error) {

    }


    // Put the data of the message into a bundle and start the corresponding IntentService.
    private void sendNotificationMatch(Bundle data) {
        String Local = data.getString(SportEventHandler.EXTRA_LOCAL);
        String Away = data.getString(SportEventHandler.EXTRA_AWAY);
        String LocalScore = data.getString(SportEventHandler.EXTRA_LOCAL_SCORE);
        String AwayScore = data.getString(SportEventHandler.EXTRA_AWAY_SCORE);
        String Status = data.getString(SportEventHandler.EXTRA_STATUS);
        String comment = data.getString(SportEventHandler.EXTRA_COMMENT);
        String matchId = data.getString(SportEventHandler.EXTRA_MATCHID);
        SportEventHandler.startActionRegularAction(this, matchId, Local, Away, LocalScore, AwayScore, Status, comment);
    }
}
