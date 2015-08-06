package com.google.android.gcm.demo.model;

public class Topic {

    private String name;
    private String url;
    private boolean subscribed;

    public Topic(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Obtiene el valor de la propiedad name.
     *
     * @return possible object is {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Define el valor de la propiedad name.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtiene el valor de la propiedad url.
     *
     * @return possible object is {@link String }
     */
    public String getUrl() {
        return url;
    }

    /**
     * Define el valor de la propiedad url.
     *
     * @param value allowed object is {@link String }
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object objeto) {
        // If the registrationId is the same, then it should be the same
        // device
        return this.url.equals(((Topic) objeto)
                .getUrl());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Topic[");
        if (name != null) {
            builder.append(" name=").append(name);
        }
        if (url != null) {
            builder.append(" url=").append(url);
        }

        builder.append(" ]");
        return builder.toString();
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}

