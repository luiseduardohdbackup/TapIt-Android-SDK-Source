package com.tapit.ads;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import com.tapit.adview.AdRequest;
import com.tapit.adview.AdViewCore;
import com.tapit.adview.TILog;
import com.tapit.adview.Utils;

import java.util.HashMap;
import java.util.Map;

public class TapItBannerAdView extends View {
    protected AdViewCore adView = null;
    protected ImageButton closeButton = null;

    protected TapItAdRequest adRequest = null;
    protected boolean autoLoad = false;


    public TapItBannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final String zone = Utils.getStringResource(context, attrs.getAttributeValue(null, "zone"));
        autoLoad = attrs.getAttributeBooleanValue(null, "autoload", true);
        TILog.w("zone: " + zone);
        TILog.w("autoLoad: " + autoLoad);
        setBackgroundColor(Color.BLUE);
        if (zone != null) {
            adRequest = new TapItAdRequest(zone);
        }
    }

    public TapItBannerAdView(Context context) {
        super(context);
        TILog.w("context constructor");
    }

    public void startServingAds() {
        if (adRequest == null) {
            throw new RuntimeException("TapItBannerAdView was not initialized properly.  "
                                        + "Please supply a TapItAdRequest.");
        }
        adRequest.setAdtype(TapItAdRequest.AdTypes.BANNER);
        TILog.w(adRequest.toUrlString(getContext()));

    }

    public void startServingAds(TapItAdRequest request) {
        adRequest = request;
        startServingAds();
    }

    public void updateLocation(double latitude, double longitude) {
        if (adRequest != null) {
            adRequest.setLocation(latitude, longitude);
        }
        else {
            TILog.w("Trying to updateLocation on un-initialized banner!");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        TILog.w("attached!");
        if (autoLoad && adRequest != null) {
            updateLocation(1.23456, 2.34567);
            startServingAds();
        }
    }
}
