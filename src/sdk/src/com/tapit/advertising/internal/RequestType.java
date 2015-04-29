package com.tapit.advertising.internal;

/**
 * Created by rajeshmuthu on 4/21/15.
 */
public enum RequestType {
    BANNER("1"),INTERSTITIAL("2"),NATIVE("15"),VIDEO("16");
    private String requestType;

    private RequestType(String rType){
        this.requestType = rType;
    }
    public String getRequestType(){
        return requestType;
    }
}
