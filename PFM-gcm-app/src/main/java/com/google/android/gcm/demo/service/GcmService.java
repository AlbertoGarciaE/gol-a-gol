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
package com.google.android.gcm.demo.service;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import static com.google.android.gcm.demo.service.SportEventHandler.EXTRA_AWAY;
import static com.google.android.gcm.demo.service.SportEventHandler.EXTRA_AWAY_SCORE;
import static com.google.android.gcm.demo.service.SportEventHandler.EXTRA_COMMENT;
import static com.google.android.gcm.demo.service.SportEventHandler.EXTRA_LOCAL;
import static com.google.android.gcm.demo.service.SportEventHandler.EXTRA_LOCAL_SCORE;
import static com.google.android.gcm.demo.service.SportEventHandler.EXTRA_STATUS;
import static com.google.android.gcm.demo.service.SportEventHandler.EXTRA_MATCHID;

import static com.google.android.gcm.demo.service.SportEventHandler.startActionRegularAction;


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {

    private LoggingService.Logger logger;

    public GcmService() {
        logger = new LoggingService.Logger(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification("Received: " + data.toString());
        sendNotificationMatch(data);
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        logger.log(Log.INFO, msg);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotificationMatch(Bundle data) {
        String Local = data.getString(EXTRA_LOCAL);
        String Away = data.getString(EXTRA_AWAY);
        String LocalScore = data.getString(EXTRA_LOCAL_SCORE);
        String AwayScore = data.getString(EXTRA_AWAY_SCORE);
        String Status = data.getString(EXTRA_STATUS);
        String comment = data.getString(EXTRA_COMMENT);
        String matchId = data.getString(EXTRA_MATCHID);
        startActionRegularAction(this, matchId, Local, Away, LocalScore, AwayScore, Status, comment);
    }
}
