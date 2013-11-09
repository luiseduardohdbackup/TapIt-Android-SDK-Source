package com.tapit.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.tapit.advertising.*;

public class MyActivity extends Activity {

    private final static String TAG = "TapIt";

    private final static String BANNER_ZONE_ID = "7979";
    private final static String VIDEO_ZONE_ID = "7981";
    private final static String INTRS_ZONE_ID = "7983";
    private final static String ADPROMPT_ZONE_ID = "7984";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // test that you've integrated properly
        // NOTE: remove this before your app goes live!
        TapItAdvertising.get().validateSetup(this);
    }

    public void simpleAdPromptExmple() {
        TapItAdPrompt adPrompt = TapItAdvertising.get().getAdPromptForZone(this, ADPROMPT_ZONE_ID);
        adPrompt.show();
    }

    public void advancedAdPromptExample() {
        // generate a customized request
        TapItAdRequest request = TapItAdvertising.get().getAdRequestBuilder(ADPROMPT_ZONE_ID)
                                                   .setTestMode(true)
                                    .getPwAdRequest();

        // get an ad instance using request
        TapItAdPrompt adPrompt = TapItAdvertising.get().getAdPrompt(this, request);

        // register for ad lifecycle callbacks
        adPrompt.setListener(new TapItAdPrompt.TapItAdPromptListener() {
            @Override
            public void adPromptDidLoad(TapItAdPrompt ad) {
                // show ad as soon as it's loaded
                Log.d(TAG, "AdPrompt Loaded");
                ad.show();
            }

            @Override
            public void adPromptDisplayed(TapItAdPrompt ad) {
                Log.d(TAG, "Ad Prompt Displayed");
            }

            @Override
            public void adPromptDidFail(TapItAdPrompt ad, String error) {
                Log.d(TAG, "Ad Prompt Error: " + error);
            }

            @Override
            public void adPromptClosed(TapItAdPrompt ad, boolean didAccept) {
                String btnName = didAccept ? "YES" : "NO";
                Log.d(TAG, "Ad Prompt Closed with \"" + btnName + "\" button");
            }
        });

        // load ad... we'll be notified when it's ready
        adPrompt.load();
    }


    public void simpleInterstitialExample() {
        TapItInterstitialAd interstitialAd = TapItAdvertising.get().getInterstitialAdForZone(this, INTRS_ZONE_ID);
        interstitialAd.show();
    }


    public void advancedInterstitialExample() {
        // generate a customized request
        TapItAdRequest request = TapItAdvertising.get().getAdRequestBuilder(INTRS_ZONE_ID)
                                                                    .setTestMode(true)
                                                        .getPwAdRequest();

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
        TapItVideoInterstitialAd videoAd = TapItAdvertising.get().getVideoInterstitialAdForZone(this, VIDEO_ZONE_ID);
        videoAd.show();
    }


    public void advancedVideoExample() {
        // generate a customized request
        TapItAdRequest request = TapItAdvertising.get().getAdRequestBuilder(VIDEO_ZONE_ID)
                                                                    .setTestMode(true)
                                                        .getPwAdRequest();

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
        bannerAdView.startRequestingAdsForZone(BANNER_ZONE_ID);
    }

    public void advancedBannerExample() {
        // find the view in your layout
        TapItBannerAdView bannerAdView = (TapItBannerAdView)findViewById(R.id.bannerAdView);

        // generate a customized request
        TapItAdRequest request = TapItAdvertising.get().getAdRequestBuilder(BANNER_ZONE_ID)
                                                                    .setTestMode(true)
                                                            .getPwAdRequest();

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

        // start banner rotating
        bannerAdView.startRequestingAds(request);
    }


    public void fireAdPrompt(View sender) {
        simpleAdPromptExmple();
//        advancedAdPromptExample();
    }

    public void fireInterstitial(View sender) {
        simpleInterstitialExample();
//        advancedInterstitialExample();
    }

    public void fireVideoInterstitial(View sender) {
        simpleVideoExample();
//        advancedVideoExample();
    }

    public void fireBanner(View sender) {
        simpleBannerExample();
//        advancedBannerExample();
    }
}
