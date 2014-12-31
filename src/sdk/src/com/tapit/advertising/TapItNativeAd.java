package com.tapit.advertising;

import android.content.Context;

public interface TapItNativeAd {

    public interface TapItNativeAdListener {
        /**
         * This event is fired once native ad content is fully downloaded
         * and is ready for display.
         *
         * @param nativeAd the native ad that is ready to be consumed.
         */
        public void nativeAdDidLoad(TapItNativeAd nativeAd);

        /**
         * This event is fired if the native ad request fails to return
         * an ad.
         *
         * @param nativeAd the native ad that failed to load
         * @param error description of the error, used for debugging purposes.
         *        error descriptions are generally not shown to the end user.
         */
        public void nativeAdDidFail(TapItNativeAd nativeAd, String error);
    }

    public void setListener(TapItNativeAdListener listener);

    /**
     * Fire off an asynchronous request to server for a native ad.
     * Use {@link #isLoaded()} or {@link TapItNativeAdListener} to determine
     * when the ad is loaded.
     */
    public void load();

    /**
     * Used to determine if ad is ready to be shown.
     * @return true if ready for display, false otherwise
     */
    public boolean isLoaded();

    /**
     * Fetch a JSON formatted string representing the native ad data.
     */
    public String getAdData();

    /**
     * Call this method when displaying native ad onscreen.
     */
    public void trackImpression();

    /**
     * Call this method when native ad is clicked.
     */
    public void click(Context ctx);
}
