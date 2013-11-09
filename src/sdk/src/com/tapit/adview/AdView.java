package com.tapit.adview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.Display;

import java.util.Map;

/**
 * Viewer of advertising. Following parameters are defined automatically, if
 * they are equal NULL: latitude - Latitude. longitude - Longitude. ua - The
 * browser user agent of the device making the request.
 */
public class AdView extends AdViewCore {
    private BannerAdSize adSize;

    /**
     * Available ad dimensions for display
     * AUTOSIZE_BANNER will attempt to fill the size allocated to the AdView
     *
     * See http://tapit.com/technology/ad-specs for a description of the other ad sizes
     */
    public enum BannerAdSize {
        AUTOSIZE_AD         ( -1,  -1),
        SMALL_BANNER        (120,  20),
        MEDIUM_BANNER       (168,  28),
        LARGE_BANNER        (216,  36),
        XL_BANNER           (300,  50),
        IPHONE_BANNER       (320,  50),
        MEDIUM_RECTANGLE    (300,  250),
        LEADERBOARD         (728,  90);

        public final int width;
        public final int height;
        BannerAdSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Creation of viewer of advertising.
     *
     * @param context
     *            - The reference to the context of Activity.
     * @param zone
     *            - The id of the zone of publisher site.
     */
    public AdView(Context context, String zone) {
        super(context, zone);
        initialize(context);
    }

    /**
     * Creation of advanced viewer of advertising. It is used for element
     * creation in a XML template.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    /**
     * Creation of advanced viewer of advertising. It is used for element
     * creation in a XML template.
     *
     * @param context
     * @param attrs
     */
    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    /**
     * Creation of advanced viewer of advertising. It is used for element
     * creation in a XML template.
     *
     * @param context
     */
    public AdView(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        adSize = BannerAdSize.AUTOSIZE_AD;
        if (adRequest != null) {
            AutoDetectedParametersSet autoDetectedParametersSet = AutoDetectedParametersSet
                    .getInstance();

            if (adRequest.getUa() == null) {
                if (autoDetectedParametersSet.getUa() == null) {
                    String userAgent = getSettings().getUserAgentString();

                    if ((userAgent != null) && (userAgent.length() > 0)) {
                        adRequest.setUa(userAgent);
                        autoDetectedParametersSet.setUa(userAgent);
                    }
                } else {
                    adRequest.setUa(autoDetectedParametersSet.getUa());
                }
            }
        }

    }

    public BannerAdSize getAdSize() {
        return adSize;
    }

    public void setAdSize(BannerAdSize adSize) {
        this.adSize = adSize;
    }

    protected void calcDimensionsForRequest(Context ctx) {
        int width = adSize.width;
        int height = adSize.height;

        if(width <= 0) {
            Display display = ((Activity) ctx).getWindowManager().getDefaultDisplay();
            int screenHeight = display.getHeight();
            int screenWidth = display.getWidth();

            int adWidth = (int)(getWidth() / mDensity);
            if(adWidth <= 0) {
                // if not width is set on view, use screen width
                adWidth = screenWidth;
            }
            int adHeight = (int)(getHeight() / mDensity);
            if(adHeight <= 0) {
                // if no height is set on view, use screen height
                adHeight = screenHeight;
            }
            for(BannerAdSize as : BannerAdSize.values()) {
                if (adSize == BannerAdSize.AUTOSIZE_AD && as == BannerAdSize.MEDIUM_RECTANGLE) {
                    // Don't consider medium rectangles when auto sizing
                    // noop
                }
                else {
                    if(as.width <= adWidth && as.height <= adHeight
                            && (as.width > width || as.height > height)) {
                        width = as.width;
                        height = as.height;
                    }
                }
            }
        }

        if ((adRequest != null)) {
            adRequest.setHeight(height);
            adRequest.setWidth(width);

            String orientation;
            int orient = ctx.getResources().getConfiguration().orientation;
            if (orient == Configuration.ORIENTATION_LANDSCAPE) {
                orientation = "l";
            } else if (orient == Configuration.ORIENTATION_UNDEFINED) {
                orientation = "x";
            }
            else {
                orientation = "p";
            }

            Map<String, String> params = adRequest.getCustomParameters();
            params.put("o", orientation);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "AttachedToWindow", "");
        initAutoDetectParametersThread();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "DetachedFromWindow", "");
        super.onDetachedFromWindow();
    }

    public void startRequestingAds(AdRequest request) {
        adRequest = request;
        update(true);
    }

    /**
     * kicks off ad loading and display
     */
    public void startUpdating() {

    }

    /**
     * stop ad from automatically reloading
     */
    @Override
    public void cancelUpdating() {
        if ((locationManager != null) && (listener != null)) {
            locationManager.removeUpdates(listener);
        }
        interruptAutoDetectParametersThread();
        super.cancelUpdating();
    }
}
