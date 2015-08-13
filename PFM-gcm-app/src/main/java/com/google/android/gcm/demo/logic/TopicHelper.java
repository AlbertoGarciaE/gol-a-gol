package com.google.android.gcm.demo.logic;

import com.google.android.gcm.demo.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usuario on 07/08/2015.
 */
public class TopicHelper {
    private List<Topic> mListTopics;
    private static TopicHelper instance;

    public static TopicHelper getInstance() {
        if (instance == null) {
            instance = new TopicHelper();
        }
        return instance;
    }

    private TopicHelper() {
        this.mListTopics = new ArrayList<>();
    }

    public List<Topic> getListTopics() {
        return mListTopics;
    }

    public void setListTopics(List<Topic> mListTopics) {
        this.mListTopics = new ArrayList<>(mListTopics);
    }

    public Topic findTopic(String url) {
        Topic aux = new Topic("auxName", url);
        return mListTopics.get(mListTopics.indexOf(aux));
    }

    public void updateTopicSubscriptionState(String url, boolean state) {
        Topic aux = new Topic("auxName", url);
        aux = mListTopics.get(mListTopics.indexOf(aux));
        aux.setSubscribed(state);
    }

    public boolean getTopicSubscriptionState(String url) {
        Topic aux = new Topic("auxName", url);
        aux = mListTopics.get(mListTopics.indexOf(aux));
        return aux.isSubscribed();
    }

    public List<Topic> getSubscribedTopics() {
        List<Topic> aux = new ArrayList<>();
        for (Topic topic : this.mListTopics) {
            if (topic.isSubscribed()) {
                aux.add(topic);
            }
        }
        return aux;
    }

}
