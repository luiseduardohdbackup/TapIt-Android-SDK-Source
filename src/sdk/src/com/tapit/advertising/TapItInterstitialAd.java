package com.tapit.advertising;

/**
 * Interstitials are full screen ads that display in their own Activity.
 *
 * Interstitials are best used at discrete stopping points in your app's flow,
 * such as at the end of a game level, or when the player dies.
 *
 * <h3>Simple example:</h3>
 * <pre>
 *     TapItInterstitialAd interstitialAd = TapItAdvertising.getInterstitialAdForZone(context, "YOUR_ZONE_ID");
 *     interstitialAd.show(); // interstitial is loaded and shown asynchronously
 * </pre>
 *
 * Instances of TapItInterstitialAd are not safe for use by multiple threads.
 */
public interface TapItInterstitialAd {

    /**
     * Implement this interface to be notified of interstitial ad lifecycle changes.
     */
    public interface TapItInterstitialAdListener {
        /**
         * This event is fired once interstitial content is fully downloaded
         * and is ready to be displayed.
         *
         * @param interstitialAd the interstitial that caused the event
         */
        public void interstitialDidLoad(TapItInterstitialAd interstitialAd);

        /**
         * This event is fired once an interstitial closes,
         * and your application is back in focus.
         *
         * @param interstitialAd the interstitial that caused the event
         */
        public void interstitialDidClose(TapItInterstitialAd interstitialAd);

        /**
         * This event is fired if the interstitial request fails to return
         * an ad.
         *
         * @param interstitialAd the interstitial that caused the event
         * @param error description of the error, used for debugging purposes.
         *        error description's are generally not shown to the end user.
         */
        public void interstitialDidFail(TapItInterstitialAd interstitialAd, String error);

        /**
         * This event is fired just before the app will be sent to the
         * background.
         *
         * @param interstitialAd the interstitial that caused the event
         */
        public void interstitialActionWillLeaveApplication(TapItInterstitialAd interstitialAd);
    }

    public TapItInterstitialAdListener getListener();

    /**
     * @param interstitialListener the listener instance that will be notified
     * of ad lifecycle events
     */
    public void setListener(final TapItInterstitialAdListener interstitialListener);

    /**
     * Used to determine if interstitial is ready to be shown.
     * @return true if ready for display, false otherwise
     */
    public boolean isLoaded();

    /**
     * Fire off an asynchronous request to server for an interstitial ad.
     * Use {@link #isLoaded()} or {@link TapItInterstitialAdListener} to determine
     * when interstitial is loaded.
     */
    public void load();

    /**
     * Display interstitial to user.  If interstitial is not loaded, {@link #load()}
     * will be called and the interstitial will be loaded and shown asynchronously.
     * Use {@link #isLoaded()} or {@link TapItInterstitialAdListener} to determine when
     * interstitial is loaded.
     */
    public void show();
}
