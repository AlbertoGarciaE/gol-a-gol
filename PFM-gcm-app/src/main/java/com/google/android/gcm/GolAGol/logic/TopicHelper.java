package com.google.android.gcm.GolAGol.logic;

import com.google.android.gcm.GolAGol.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds a topic list as a singleton, and
 * implements the most common actions to manage the list of topics
 */
public class TopicHelper {
    private List<Topic> mListTopics;
    private static TopicHelper instance;

    /**
     * Get an instance of the class in order to access the list
     *
     * @return TopicHelper
     */
    public static TopicHelper getInstance() {
        if (instance == null) {
            instance = new TopicHelper();
        }
        return instance;
    }

    private TopicHelper() {
        this.mListTopics = new ArrayList<>();
    }

    /**
     * Get the full list of topics
     *
     * @return List<Topic>
     */
    public List<Topic> getListTopics() {
        return mListTopics;
    }

    /**
     * Owerride the current list of topics with a copy of the given list of topics
     *
     * @param mListTopics The list of topics used to override the actual list
     */
    public void setListTopics(List<Topic> mListTopics) {
        this.mListTopics = new ArrayList<>(mListTopics);
    }

    /**
     * Find the topic with the url given
     *
     * @param url Unique url that identifies a topic
     * @return Topic
     */
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

    /**
     * Update the subscription state of a Topic in the list of topics
     *
     * @param url   Unique url that identifies a topic
     * @param state The new subscription state
     */
    public void updateTopicSubscriptionState(String url, boolean state) {
        Topic aux = findTopic(url);
        if (aux != null) {
            aux.setSubscribed(state);
        }
    }

    /**
     * Return the subscription state of the Topic identifiyed with the given url
     *
     * @param url Unique url that identifies a topic
     * @return The actual subscription state of the Topic
     */
    public boolean getTopicSubscriptionState(String url) {
        boolean result = false;
        Topic aux = findTopic(url);
        if (aux != null) {
            result = aux.isSubscribed();
        }
        return result;
    }

    /**
     * Get the list of subscribed topics
     *
     * @return List<Topic>
     */
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
