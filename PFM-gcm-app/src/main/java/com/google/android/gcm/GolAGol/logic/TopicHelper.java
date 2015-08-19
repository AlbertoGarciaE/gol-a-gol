package com.google.android.gcm.GolAGol.logic;

import com.google.android.gcm.GolAGol.model.Topic;

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
        int index = mListTopics.indexOf(aux);
        if (index >= 0) {
            aux = mListTopics.get(index);
        } else {
            aux = null;
        }
        return aux;
    }

    public void updateTopicSubscriptionState(String url, boolean state) {
        Topic aux = findTopic(url);
        if (aux != null) {
            aux.setSubscribed(state);
        }
    }

    public boolean getTopicSubscriptionState(String url) {
        boolean result = false;
        Topic aux = findTopic(url);
        if (aux != null) {
            result = aux.isSubscribed();
        }
        return result;
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
