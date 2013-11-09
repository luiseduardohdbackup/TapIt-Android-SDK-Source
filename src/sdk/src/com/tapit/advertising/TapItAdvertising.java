package com.tapit.advertising;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;
import com.tapit.advertising.*;
import com.tapit.advertising.internal.*;
import com.tapit.adview.Utils;

public class TapItAdvertising {

    private static TapItAdvertising instance = null;

    private TapItAdvertising() {}

    /**
     * Get an instance of the TapItAdvertising which allows you to instanciate
     * ad objects
     * @return a TapItAdvertising instance
     */
    public static TapItAdvertising get() {
        if (instance == null) {
            instance = new TapItAdvertising();
        }
        return instance;
    }

    /**
     * Use this utility method to test if you've configured your AndroidManifest.xml
     * properly.
     * @param context your application's context
     * @throws IllegalStateException if your manifest is not configured properly.
     *         The exception description explains what is mis-configured.
     */
    public void validateSetup(Context context) {
        // test that required permissions are specified
        if (!Utils.hasPermission(context, Manifest.permission.INTERNET)) {
            throw new IllegalStateException("TapItAdvertising requires the \"INTERNET\" permission.");
        }

        // test that TapItAdActivity is registered
        PackageManager pm = context.getPackageManager();
        ComponentName cn = new ComponentName(context.getPackageName(), TapItAdActivity.class.getCanonicalName());
        try {
            pm.getActivityInfo(cn, PackageManager.GET_META_DATA);
            Toast.makeText(context, "TapItAdvertising module was set up properly!", Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("TapItAdvertising requires the \"TapItAdActivity\" activity.");
        }

    }


    /*******************************************************
     * TapItAdRequest factory methods
     *******************************************************/


    /**
     * TODO add docs here!
     * @param zone Identifier of ad placement to be loaded.
     * @return A TapItAdRequest instance that can be used to initialize an ad
     */
    public TapItAdRequest getAdRequestForZone(String zone) {
        return getAdRequestBuilder(zone).getPwAdRequest();
    }

    /**
     * TODO add docs here!
     * @param zone Identifier of ad placement to be loaded.
     * @return A TapItAdRequestBuilder instance that can be used to construct a TapItAdRequest
     */
    public TapItAdRequest.Builder getAdRequestBuilder(String zone) {
        return new AdRequestImpl.BuilderImpl(zone);
    }


    /*******************************************************
     * TapItAdPrompt factory methods
     *******************************************************/


    /**
     * Factory method which generates AdPrompt object using zone.
     * @param context Application context that will be used to show Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return An AdPrompt object that is ready to be loaded.
     *         Call {@link com.tapit.advertising.TapItAdPrompt#load()} to initiate ad request.
     * @see com.tapit.advertising.TapItAdPrompt
     */
    public TapItAdPrompt getAdPromptForZone(Context context, String zone) {
        return AdPromptImpl.getAdPromptForZone(context, zone);
    }

    /**
     * Factory method which generates AdPrompt object using TapItAdRequest.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return An AdPrompt object that is ready to be loaded.
     *         Call {@link TapItAdPrompt#load()} to initiate ad request.
     * @see TapItAdPrompt
     */
    public TapItAdPrompt getAdPrompt(Context context, TapItAdRequest request) {
        return AdPromptImpl.getAdPrompt(context, request);
    }

    /*******************************************************
     * TapItBannerAdView factory methods
     *******************************************************/


    /**
     * Factory method which generates Banner ad object.
     * {@link com.tapit.advertising.TapItBannerAdView#getBannerAd(android.content.Context)}
     * @see com.tapit.advertising.TapItBannerAdView
     */
    public TapItBannerAdView getAdBannerView(Context context) {
        return TapItBannerAdView.getBannerAd(context);
    }


    /*******************************************************
     * TapItInterstitialAd factory methods
     *******************************************************/


    /**
     * Factory method which generates Interstitial Ad object.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return An interstital ad object that is ready to be loaded.
     *         Call {@link com.tapit.advertising.TapItInterstitialAd#load()} to initiate ad request.
     * @see com.tapit.advertising.TapItInterstitialAd
     */
    public TapItInterstitialAd getInterstitialAd(Context context, TapItAdRequest request) {
        return InterstitialAdImpl.getInterstitialAd(context, request);
    }

    /**
     * Factory method which generates Interstitial Ad object
     * @param context Application context that will be used to show Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return An interstital ad object that is ready to be loaded.
     *         Call {@link TapItInterstitialAd#load()} to initiate ad request.
     * @see TapItInterstitialAd
     */
    public TapItInterstitialAd getInterstitialAdForZone(Context context, String zone) {
        return InterstitialAdImpl.getInterstitialAdForZone(context, zone);
    }


    /*******************************************************
     * TapItVideoInterstitialAd factory methods
     *******************************************************/


    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return A video interstital ad object that is ready to be loaded.
     *         Call {@link TapItVideoInterstitialAd#load()} to initiate ad request.
     * @see TapItVideoInterstitialAd
     */
    public TapItVideoInterstitialAd getVideoInterstitialAd(Context context, TapItAdRequest request) {
        return VideoInterstitialAdImpl.getVideoInterstitialAd(context, request);
    }

    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Video Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return A video interstital ad object that is ready to be loaded.
     *         Call {@link TapItVideoInterstitialAd#load()} to initiate ad request.
     * @see TapItVideoInterstitialAd
     */
    public TapItVideoInterstitialAd getVideoInterstitialAdForZone(Context context, String zone) {
        return VideoInterstitialAdImpl.getVideoInterstitialAdForZone(context, zone);
    }

//TODO document how to capture google play referral codes
//    /**
//     * Track application install the first time it is loaded.
//     * @param context application content
//     * @throws NullPointerException if context is null
//     */
//    public void trackInstall(Context context) {
//        trackInstall(context, null);
//    }
//
//    /**
//     * Track application install the first time it is loaded.
//     * @param context application context
//     * @param offer the offer for which this install can be attributed to
//     * @throws NullPointerException if context is null
//     */
//    public void trackInstall(Context context, String offer) {
//        //TODO implement me!
//    }
//
//    @Override
//    protected void onActivityStart(Activity activity) {
//        super.onActivityStart(activity);
//        if (BuildConfig.DEBUG) {
//            validateSetup(activity);
//        }
//    }
}
