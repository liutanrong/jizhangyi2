package com.liu.Account.network.beans;

/**
 * Created by xing on 2016/9/5.
 */
public class OkReceive {
    public long timestamp;
    public String response;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
