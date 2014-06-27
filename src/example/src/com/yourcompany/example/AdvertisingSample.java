package com.yourcompany.example;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.tapit.advertising.*;
import com.tapit.core.TapItLog;


public class AdvertisingSample extends Activity {

    private final static String TAG = "AdvertisingSample";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // enable debug logs during development
        TapItLog.setShowLog(true);

        // test that you've integrated properly
        // NOTE: remove this before your app goes live!
        TapItAdvertising.get().validateSetup(this);
    }

    public void simpleInterstitialExample() {
        String zoneId = getResources().getString(R.string.intrs_zone_id);
        TapItInterstitialAd interstitialAd = TapItAdvertising.get().getInterstitialAdForZone(this, zoneId);
        interstitialAd.show();
    }


    public void advancedInterstitialExample() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.intrs_zone_id);

        TapItAdRequest request = TapItAdvertising.get().getAdRequestBuilder(zoneId)
//                                                                // enable during the development phase
//                                                                .setTestMode(true)
                                                        .getTapItAdRequest();

        // get an ad instance using request
        TapItInterstitialAd interstitialAd = TapItAdvertising.get().getInterstitialAd(this, request);

        // register for ad lifecycle callbacks
        interstitialAd.setListener(new TapItInterstitialAd.TapItInterstitialAdListener() {
            @Override
            public void interstitialDidLoad(TapItInterstitialAd ad) {
                // show ad as soon as it's loaded
                Log.d(TAG, "Interstitial Did Load");
                ad.show();
            }

            @Override
            public void interstitialDidClose(TapItInterstitialAd ad) {
                Log.d(TAG, "Interstitial Did Close");
            }

            @Override
            public void interstitialDidFail(TapItInterstitialAd ad, String error) {
                Log.d(TAG, "Interstitial Did Fail: " + error);
            }

            @Override
            public void interstitialActionWillLeaveApplication(TapItInterstitialAd ad) {
                Log.d(TAG, "Interstitial Will Leave App");
            }
        });

        // load ad... we'll be notified when it's ready
        interstitialAd.load();
    }


    public void simpleVideoExample() {
        String zoneId = getResources().getString(R.string.video_zone_id);
        TapItVideoInterstitialAd videoAd = TapItAdvertising.get().getVideoInterstitialAdForZone(this, zoneId);
        videoAd.show();
    }


    public void advancedVideoExample() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.video_zone_id);
        TapItAdRequest request = TapItAdvertising.get().getAdRequestBuilder(zoneId)
                                                                // enable during the development phase
                                                                .setTestMode(true)
                                                        .getTapItAdRequest();

        // get an ad instance using request
        TapItVideoInterstitialAd videoAd = TapItAdvertising.get().getVideoInterstitialAd(this, request);

        // register for ad lifecycle callbacks
        videoAd.setListener(new TapItVideoInterstitialAd.TapItVideoInterstitialAdListener() {
            @Override
            public void videoInterstitialDidLoad(TapItVideoInterstitialAd ad) {
                // show ad as soon as it's loaded
                Log.d(TAG, "VideoAd Did Load");
                ad.show();
            }

            @Override
            public void videoInterstitialDidClose(TapItVideoInterstitialAd ad) {
                Log.d(TAG, "videoInterstitialDidClose");
            }

            @Override
            public void videoInterstitialDidFail(TapItVideoInterstitialAd ad, String error) {
                Log.d(TAG, "videoInterstitialDidFail: " + error);
            }

            @Override
            public void videoInterstitialActionWillLeaveApplication(TapItVideoInterstitialAd ad) {
                Log.d(TAG, "videoInterstitialActionWillLeaveApplication");
            }
        });

        // load ad... we'll be notified when it's ready
        videoAd.load();
    }


    public void simpleBannerExample() {
        TapItBannerAdView bannerAdView = (TapItBannerAdView)findViewById(R.id.bannerAdView);
        String zoneId = getResources().getString(R.string.banner_zone_id);
        bannerAdView.startRequestingAdsForZone(zoneId);
    }

    public void advancedBannerExample() {
        Log.d(TAG, "advancedBannerExample");
        // find the view in your layout
        TapItBannerAdView bannerAdView = (TapItBannerAdView)findViewById(R.id.bannerAdView);

        // Banner rotation interval; defaults to 60 seconds.
//        bannerAdView.setAdUpdateInterval(0); // no auto rotation
        bannerAdView.setAdUpdateInterval(30); // rotate every 30 seconds.

        // generate a customized request
        String zoneId = getResources().getString(R.string.banner_zone_id);

        TapItAdRequest request = TapItAdvertising.get().getAdRequestBuilder(zoneId)
                                                                    // enable during the development phase
                                                                    .setTestMode(true)

//                                                                    // enable automatic gps based location tracking
//                                                                    .setLocationTrackingEnabled(true)

//                                                                    // optional keywords for custom targeting
//                                                                    .setKeywords(Arrays.asList("keyword1", "keyword2"))
                                                            .getTapItAdRequest();

        // register for ad lifecycle callbacks
        bannerAdView.setListener(new TapItBannerAdView.BannerAdListener() {
            @Override
            public void onReceiveBannerAd(TapItBannerAdView ad) {
                Log.d(TAG, "Banner onReceiveBannerAd");
            }

            @Override
            public void onBannerAdError(TapItBannerAdView ad, String errorMsg) {
                Log.d(TAG, "Banner onBannerAdError: " + errorMsg);
            }

            @Override
            public void onBannerAdFullscreen(TapItBannerAdView ad) {
                Log.d(TAG, "Banner onBannerAdFullscreen");
            }

            @Override
            public void onBannerAdDismissFullscreen(TapItBannerAdView ad) {
                Log.d(TAG, "Banner onBannerAdDismissFullscreen");
            }

            @Override
            public void onBannerAdLeaveApplication(TapItBannerAdView ad) {
                Log.d(TAG, "Banner onBannerAdLeaveApplication");
            }
        });

//        // Optionally set location manually.
//        double lat = 40.7787895;
//        double lng = -73.9660945;
//        bannerAdView.updateLocation(lat, lng);

        // start banner rotating
        bannerAdView.startRequestingAds(request);
    }


    public void fireInterstitial(View sender) {
//        simpleInterstitialExample();
        advancedInterstitialExample();
    }

    public void fireVideoInterstitial(View sender) {
//        simpleVideoExample();
        advancedVideoExample();
    }

    public void fireBanner(View sender) {
//        simpleBannerExample();
        advancedBannerExample();
    }
}
