package com.tapit.advertising.internal;

import android.content.Context;
import android.os.Handler;
import com.tapit.core.TapItLog;


public abstract class MockAdManager extends AdManager implements WebUtils.AsyncHttpResponseListener {
    private static final String TAG = "TapIt";

    public MockAdManager(ResultsCallback callback) {
        super(callback);
    }

    @Override
    public void submitRequest(final Context context, final AdRequestUrlBuilder requestUrlBuilder) {
        if (requestUrlBuilder == null) {
            throw new IllegalArgumentException("requestUrlBuilder must be set");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must be set");
        }

        TapItLog.w(TAG, "USING MOCK AdManager!  Not making a real call to the server!");

        long delay = (long)(Math.random() * 2000);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    asyncResponse(getMockResponse());
                } catch (Exception e) {
                    asyncError(e);
                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, delay);

    }

    public abstract String getMockResponse() throws Exception;

    @Override
    public void asyncResponse(String responseStr) {
        TapItLog.d(TAG, "Response: " + responseStr);

        AdResponseBuilder responseBuilder = new AdResponseBuilder();
        try {
            AdResponseBuilder.AdResponse adResponse = responseBuilder.buildFromString(responseStr);
            callback.onSuccess(adResponse);
        } catch (AdResponseBuilder.NoAdReturnedException e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void asyncError(Throwable throwable) {
        TapItLog.d(TAG, "Server request failed", throwable);
        callback.onError("Failed to retrieve ad.");
    }}
