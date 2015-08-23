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
package com.google.android.gcm.GolAGol.model;

/**
 Constants used for persisting the app's objects */
public class Constants {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";
    public static final String USER_NAME = "userName";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final int TOKEN_ACTION_GET = 1;
    public static final int TOKEN_ACTION_ERASE = 0;

    //Services actions

    //Shared preferences
    public static final String PREF_TOPIC_LIST = "listOfTopics";

    //RESTfull service paths
    public static final String REGISTER_IN_SERVER = "/RESTService/registerDevice";
    public static final String UNREGISTER_IN_SERVER = "/RESTService/unregisterDevice";
    public static final String GET_TOPIC_LIST = "/RESTService/getTopicList";

    public static final String SERVER_URL_ROOT = "http://192.168.1.40:8080";

    public static final String REST_PARAM_REG_ID = "regId";
    public static final String REST_PARAM_DEVICE_NAME = "deviceName";

    public static final String NOTIFICATION_OPEN_ACTION = "gcm_test_app_notification_click_action";

    private Constants() {

    }

}
