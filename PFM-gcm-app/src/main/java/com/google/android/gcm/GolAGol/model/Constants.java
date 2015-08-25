package com.google.android.gcm.GolAGol.model;

/**
 * Constants used for persisting the app's objects
 */
public class Constants {

    //UI actions
    public static final String ACTION_UNSUBSCRIBE = "actionUnsubscribe";
    public static final String ACTION_SUBSCRIBE = "actionSubscribe";
    public static final String ACTION_SHOW_LOG = "actionShowMatchLog";
    public static final String ACTION_HIDE_LOG = "actionHideMatchLog";
    public static final String ACTION_REFRESH_UI = "refreshUi";

    //Services actions
    public static final String ACTION_SCORE = "score";
    public static final String ACTION_REGULAR_ACTION = "regularAction";
    public static final String ACTION_REGISTRATION_GET_TOKEN = "getToken";
    public static final String ACTION_REGISTRATION_ERASE_TOKEN = "eraseToken";
    public static final String ACTION_PUBSUB_SUBSCRIBE = "actionPubSubSubscribe";
    public static final String ACTION_PUBSUB_UNSUBSCRIBE = "actionPubSubUnsubscribe";

    //Shared preferences
    public static final String PREF_TOPIC_LIST = "listOfTopics";
    public static final String PREF_MATCHES_LIST = "listOfMatches";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";
    public static final String USER_NAME = "userName";

    //RESTfull service URLs
    public static final String SERVER_URL_ROOT = "https://gcmtooltfm-tfmserver.rhcloud.com";
    public static final String REGISTER_IN_SERVER = "/RESTService/registerDevice";
    public static final String UNREGISTER_IN_SERVER = "/RESTService/unregisterDevice";
    public static final String GET_TOPIC_LIST = "/RESTService/getTopicList";


    public static final String REST_PARAM_REG_ID = "regId";
    public static final String REST_PARAM_DEVICE_NAME = "deviceName";

    public static final String NOTIFICATION_OPEN_ACTION = "gcm_test_app_notification_click_action";
    // IntentService can perform this actions

    //Actions for the intent service


    private Constants() {

    }

}
