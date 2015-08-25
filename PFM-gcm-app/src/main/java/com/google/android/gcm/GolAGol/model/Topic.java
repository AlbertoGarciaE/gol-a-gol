package com.google.android.gcm.GolAGol.model;

/**
 * This class represent a topic that the user can subscribe to
 */
public class Topic {

    private String name;
    private String url;
    private boolean subscribed;

    public Topic(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return url.equals(topic.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", subscribed=" + subscribed +
                '}';
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}

