package com.tapit.advertising;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.tapit.advertising.internal.AdRequestImpl;
import com.tapit.adview.AdView;
import com.tapit.adview.AdViewCore;
import com.tapit.adview.Utils;

/**
 * Banners are ads that are shown within the layout of your app.
 *
 * <h3>XML only implementation example:</h3>
 * <pre>
 * </pre>
 *
 * <h3>Simple code implementation example:</h3>
 * <pre>
 *     TapItBannerAdView bannerView = TapItBannerAdView.getBannerAd(context);
 *
 *     // add bannerView to activity layout...
 *
 *     bannerView.startRequestingAdsForZone("YOUR_ZONE_ID");
 * </pre>
 *
 * Instances of TapItBannerAdView are not safe for use by multiple threads.
 */
//TODO should View be split out into a separate class so we can use a static factory?
public final class TapItBannerAdView extends ViewGroup {

    /**
     * Implement this interface to be notified of banner ad lifecycle changes.
     */
    public interface BannerAdListener {

        /**
         * An ad was received and displayed.
         * @param bannerAd the banner ad that loaded
         */
        public void onReceiveBannerAd(TapItBannerAdView bannerAd);

        /**
         * An error occurred while requesting an ad.  Possible errors include network
         * failure or no ad inventory to display for this session.
         * @param bannerAd the banner ad which experienced an error
         * @param errorMsg description of the error that occurred.  Error messages are
         * informational and are not generally displayed to the user.
         */
        public void onBannerAdError(TapItBannerAdView bannerAd, String errorMsg);

        /**
         * Called just before an ad will go full-screen, covering up your app.
         * @param bannerAd the banner ad that is about to cover up your app
         */
        public void onBannerAdFullscreen(TapItBannerAdView bannerAd);

        /**
         * Called once a full screen ad has closed, leaving your app in the foreground
         * @param bannerAd the banner ad that just returned control to your app
         */
        public void onBannerAdDismissFullscreen(TapItBannerAdView bannerAd);

        /**
         * Called just before an ad interaction will start a new app, sending yours to the background.
         * @param bannerAd the banner ad that is causing your app to go into the background
         */
        public void onBannerAdLeaveApplication(TapItBannerAdView bannerAd);
    }

    public TapItBannerAdView(Context context) {
        super(context);
        legacyBannerAdView = new AdView(context);
        // stop poor implementation from loading immediately
        stopRequestingAds();
        addView(legacyBannerAdView);
    }

    public TapItBannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        legacyBannerAdView = new AdView(context);
        // stop poor implementation from loading immediately
        stopRequestingAds();
        addView(legacyBannerAdView);
        initAttributes(attrs, 0);
    }

    public TapItBannerAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        legacyBannerAdView = new AdView(context);
        // stop poor implementation from loading immediately
        stopRequestingAds();
        addView(legacyBannerAdView);
        initAttributes(attrs, defStyle);
    }

    public static TapItBannerAdView getBannerAd(Context context) {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }

        return new TapItBannerAdView(context);
    }

    /**
     * Start requesting ads for the zone provided.
     * @param zone Identifier of ad placement to be loaded.
     */
    public final void startRequestingAdsForZone(String zone) {
//        AdRequestImpl request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        TapItAdRequest request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        startRequestingAds(request);
    }

    /**
     * Start requesting ads, using the {@link com.tapit.advertising.internal.AdRequestImpl} instance provided.
     * @param request the ad request to be used while making requests
     */
    public final void startRequestingAds(TapItAdRequest request) {
        if (request == null) {
            throw new NullPointerException("request cannot be null");
        }

//        if (adRequest != null) {
//            //TODO should we loosen this restriction?
//            throw new IllegalStateException("AdRequestImpl was already set. Use resumeRequestingAds() instead.");
//        }

        adRequest = request;
        resumeRequestingAds();
    }

    /**
     * Restarts requesting ads from server on the ad update interval.
     * @throws IllegalStateException if a valid {@link com.tapit.advertising.internal.AdRequestImpl}
     * was not provided earlier
     */
    public void resumeRequestingAds() {
        legacyBannerAdView.startRequestingAds(AdRequestImpl.asImplAdRequest(adRequest));
        legacyBannerAdView.setUpdateTime(getAdUpdateInterval());
    }

    /**
     * stops banner ad from requesting new ads
     */
    public void stopRequestingAds() {
        legacyBannerAdView.cancelUpdating();
        legacyBannerAdView.setUpdateTime(9999999);
        autoLoad = false;
    }

    /**
     * Sets the interval at which a new ad is requested from the server.
     * @param delaySeconds number of seconds to wait between requesting an ad.
     * Set to 0 to disable auto update.
     */
    public void setAdUpdateInterval(int delaySeconds) {
        if (delaySeconds < 0) {
            throw new IllegalArgumentException("delaySeconds cannot be negative");
        }

        //TODO handle disabling update interval

        adUpdateIntervalSeconds = delaySeconds;
        legacyBannerAdView.setUpdateTime(adUpdateIntervalSeconds);
    }

    /**
     *
     * @return the current ad update interval, in seconds
     */
    public int getAdUpdateInterval() {
        return adUpdateIntervalSeconds;
    }

    /**
     *
     * @param listener the listener instance that will be notified
     * of ad lifecycle events
     */
    public void setListener(final BannerAdListener listener) {
        if (listener != null) {
            legacyBannerAdView.setOnAdDownload(new AdViewCore.OnAdDownload() {
                @Override
                public void begin(AdViewCore adView) {
                    // noop
                }

                @Override
                public void end(AdViewCore adView) {
                    listener.onReceiveBannerAd(TapItBannerAdView.this);
                }

                @Override
                public void error(AdViewCore adView, String error) {
                    listener.onBannerAdError(TapItBannerAdView.this, error);
                }

                @Override
                public void clicked(AdViewCore adView) {
                    // noop
                }

                @Override
                public void willPresentFullScreen(AdViewCore adView) {
                    listener.onBannerAdFullscreen(TapItBannerAdView.this);
                }

                @Override
                public void didPresentFullScreen(AdViewCore adView) {
                    // noop
                }

                @Override
                public void willDismissFullScreen(AdViewCore adView) {
                    listener.onBannerAdDismissFullscreen(TapItBannerAdView.this);
                }

                @Override
                public void willLeaveApplication(AdViewCore adView) {
                    listener.onBannerAdLeaveApplication(TapItBannerAdView.this);
                }
            });
        }
        else {
            legacyBannerAdView.setOnAdDownload(null);
        }
        this.listener = listener;
    }

    /**
     *
     * @return the current BannerAdListener object
     */
    public BannerAdListener getListener() {
        return this.listener;
    }

    /**
     *
     * @return the width, in pixels, of the currently loaded ad,
     * or -1 if no ad is currently available
     */
    public int getCurrentAdWidth() {
        return legacyBannerAdView.getAdWidth();
    }

    /**
     *
     * @return the height, in pixels, of the currently loaded ad,
     * or -1 if no ad is currently available
     */
    public int getCurrentAdHeight() {
        return legacyBannerAdView.getAdHeight();
    }

    /**
     * Set the location coordinates for geo-targeting.
     * @param latitude the latitude in decimal degrees
     * @param longitude the longitude in decimal degrees
     */
    public void updateLocation(double latitude, double longitude) {
        legacyBannerAdView.setLatitude(String.valueOf(latitude));
        legacyBannerAdView.setLongitude(String.valueOf(longitude));
    }


    /**************************************************************
     * Impl
     **************************************************************/


    static final int REFRESH_DELAY_SECONDS = 60;
    private static final String TAG = "TapIt";

    private final AdView legacyBannerAdView;
    private BannerAdListener listener = null;
    private TapItAdRequest adRequest = null;
    private int adUpdateIntervalSeconds = REFRESH_DELAY_SECONDS;

    /**
     * Used by xml implementation to determine if ad requests should start
     * immediately after banner view is attached to the view hierarchy.
     */
    private boolean autoLoad = true;

    /**
     * Used to pause banner rotation when app goes into background
     */
    private boolean isAttached = false;

    private void initAttributes(AttributeSet attrs, int defStyle) {
        final String zone = Utils.getStringResource(getContext(), attrs.getAttributeValue(null, "zone"));
        boolean autoLoadDefault = (zone != null);
        autoLoad = attrs.getAttributeBooleanValue(null, "auto_load", autoLoadDefault);
        int adUpdateIntervalSeconds = Utils.getIntegerResource(getContext(),
                attrs.getAttributeValue(null, "update_interval"), REFRESH_DELAY_SECONDS);

        setAdUpdateInterval(adUpdateIntervalSeconds);
        final boolean isTestMode = attrs.getAttributeBooleanValue(null, "test_mode", false);

        if (zone != null) {
            adRequest = new AdRequestImpl.BuilderImpl(zone)
                    .setTestMode(isTestMode)
                    .getPwAdRequest();
        }

        if (autoLoad && zone == null) {
            throw new IllegalStateException("'auto_load' attribute cannot be used without 'zone' attribute.");
        }
    }


    /**************************************************************
     * View overrides
     **************************************************************/


    @Override
    protected void onAttachedToWindow() {
//        TapItLog.d(TAG, "onAttachedToWindow()");
        isAttached = true;
        if(autoLoad && adRequest != null) {
            // banner was created via layout xml and is set to auto_load
            // start the request loop

            final ViewTreeObserver vto = getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                    TapItLog.d(TAG, "onGlobalLayout()");
                    vto.removeOnGlobalLayoutListener(this);
                    resumeRequestingAds();

                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
//        TapItLog.d(TAG, "onVisibilityChanged(" + changedView + ", " + visibility + ")");
        super.onVisibilityChanged(changedView, visibility);
        if (isAttached && visibility != View.VISIBLE) {
            // stop making ad requests when app goes into the background
            stopRequestingAds();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        TapItLog.d(TAG, "TapItBannerAdView.onMeasure(...)");

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getMetrics(metrics);
        float mDensity = metrics.density;

        int maxWidth = Math.max(legacyBannerAdView.getAdWidth(), 0);
        int maxHeight = Math.max(legacyBannerAdView.getAdHeight(), 0);
//        TapItLog.d(TAG, "mw: " + maxWidth + ", mh: " + maxHeight);

        maxWidth = (int)(maxWidth * mDensity + 0.5);
        maxHeight = (int)(maxHeight * mDensity + 0.5);

//        TapItLog.d(TAG, "mw: " + maxWidth + ", mh: " + maxHeight);

        int resolvedWidth = resolveSize(maxWidth, widthMeasureSpec);
        int resolvedHeight = resolveSize(maxHeight, heightMeasureSpec);

        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        TapItLog.d(TAG, "TapItBannerAdView.onLayout(" + changed + ", " + l + ", " + t + ", " + r + ", " + b + ")");

        int bannerWidth = r - l;
        int bannerHeight = b - t;
//        TapItLog.d(TAG, "Setting adview layout to (" + l + ", " + t + ", " + r + ", " + b + ")");
//        TapItLog.d(TAG, "Setting adview layout to (" + 0 + ", " + 0 + ", " + bannerWidth + ", " + bannerHeight + ")");

        legacyBannerAdView.layout(0, 0, bannerWidth, bannerHeight);
    }
}
