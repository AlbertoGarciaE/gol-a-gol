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
package com.google.android.gcm.GolAGol.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {

    public GcmService() {

    }

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



    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
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
