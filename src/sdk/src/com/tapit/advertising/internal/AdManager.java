package com.tapit.advertising.internal;

import android.content.Context;
import com.tapit.core.TapItLog;

public class AdManager implements WebUtils.AsyncHttpResponseListener {
    private static final String TAG = "TapIt";

    public interface ResultsCallback {
        public void onSuccess(AdResponseBuilder.AdResponse response);
        public void onError(String errorMsg);
    }

    protected final ResultsCallback callback;

    public AdManager(ResultsCallback callback) {
        this.callback = callback;
    }

    public void submitRequest(final Context context, final AdRequestUrlBuilder requestUrlBuilder) {
        if (requestUrlBuilder == null) {
            throw new IllegalArgumentException("requestUrlBuilder must be set");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must be set");
        }

        DeviceCapabilities.fetchAdvertiserInfo(context, new DeviceCapabilities.AdvertisingInfoCallback() {
            @Override
            public void advertisingInfoReceived(DeviceCapabilities.AdvertisingInfo advertisingInfo) {
                requestUrlBuilder.setAdvertisingInfo(advertisingInfo);
                // fire off server request
                String url = requestUrlBuilder.buildUrl();
                WebUtils.asyncHttpRequest(context, url, AdManager.this);
            }
        });
    }

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
    }
}
