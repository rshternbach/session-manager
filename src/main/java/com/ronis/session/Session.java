package com.ronis.session;

/**
 * POJO class representing session requested from session api
 */
public class Session {

    private String id;

    private boolean expired;

    public Session() {
    }

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isExpired() {
        return expired;
    }

    public void expire() {
        expired = true;
    }

    @Override
    public String toString() {
        return "Session [id=" + id + ", expired=" + expired + "]";
    }

}
