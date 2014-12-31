package com.tapit.advertising;

import android.content.Context;

import java.util.List;

public interface TapItAdLoader<T> {
    public interface TapItAdLoaderListener<T> {
        public void onSuccess(TapItAdLoader loader, List<T> ads);
        public void onFail(TapItAdLoader loader, String error);
    }

    /**
     * Requests multiple ads, posting them to {@link com.tapit.advertising.TapItAdLoader.TapItAdLoaderListener} when
     * ads are returned from server.
     * @param context the application context to use while build request
     * @param request the request parameters to be submitted to the ad server
     * @param maxAdsRequested the maximum number of ads to be returned.  Fewer ads
     * may be returned based on inventory availability.
     * @param callback the listener to be notified when request succeeds/fails
     */
    public void multiLoad(Context context, TapItAdRequest request, int maxAdsRequested, TapItAdLoaderListener<T> callback);
}
