package com.ronis.worker;

import com.ronis.session.Session;

/**
 * POJO class representing data for GDS api
 */
public class GdsApiData {

    private String requestData;

    private String responseData;

    private Session session;

    public GdsApiData() {
    }

    public GdsApiData(String requestData) {
        this.requestData = requestData;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "GdsApiData [requestData=" + requestData + ", responseData=" + responseData + ", session=" + session
                + "]";
    }

}
