TapIt Android SDK
=================

Version 2.1.4

This is the Android SDK for the TapIt! mobile ad network. Go to http://tapit.com/ for more details and to sign up.

###[Download TapIt SDK](https://github.com/tapit/TapIt-Android-SDK-Source/raw/master/dist/TapItSDK.zip)<br/>
###[Example project source](https://github.com/tapit/TapIt-Android-SDK-Source/tree/master/src/example)


Requrements:
------------
* Android SDK 2.2+ (API level 8) or above
* Google Play Services to enable Advertising Id support (Recommended) [Installation Instructions](https://developer.android.com/google/play-services/id.html)

Usage:
------
*We've streamlined our API as of v1.8.0, but still support previous integrations.
 Check the [Old SDK Docs](https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/README_LEGACY.md)
for legacy API documentation.*

* To install, extract the [TapIt SDK Archive](https://github.com/tapit/TapIt-Android-SDK-Source/raw/master/dist/TapItSDK.zip) into your project's `/libs` folder, and add `TapItSDK.jar` into the project's build path:

* Set `TapItSDK.jar` to be exported as part of your apk file:

* Update your `AndroidManifest.xml` to include the following permissions and activity:

````xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>

<!-- Optional permissions to enable ad geotargeting
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
-->

<!-- inside of the application tag: -->
<activity
    android:name="com.tapit.advertising.internal.TapItAdActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize" />


[Configure Google Play Services](https://developer.android.com/google/play-services/setup.html) (Recommended)

````
See [AndroidManifest.xml](https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/AndroidManifest.xml) for an example manifest file.

**NOTE:** Zones correspond to a specific ad type, which is specified through the TapIt dashboard.  Please ensure that you use the correct Zone ID for your ad units or you may experience un-expected results.

A sample project is included in this repo.  See [Example Code](https://github.com/tapit/TapIt-Android-SDK-Source/tree/master/src/example) for a live demo.


Native Ad Usage
---------------
Native ads are advertisments designed to fit naturally into your app's look and feel.  Pre-defined ad features
are provided as a JSON payload which your app consumes in a template that follows your UI's theme.

*Example Code Usage*
````java
import com.tapit.advertising.*;

// ...

String zoneId = "YOUR_NATIVE_AD_ZONE_ID";
TapItNativeAd nativeAd = TapItAdvertising.get().getNativeAdForZone(context, zoneId);
nativeAd.setListener(new TapItNativeAd.TapItNativeAdListener() {
    @Override
    public void nativeAdDidLoad(TapItNativeAd nativeAd) {
        try {
            renderUiFromNativeAd(nativeAd);

        } catch (JSONException e) {
            // log error and discard this native ad instance
        }
    }

    @Override
    public void nativeAdDidFail(TapItNativeAd nativeAd, String errMsg) {
        // The ad failed to load, errMsg describes why.
        // Error messages are not intended to be displayed to the user
    }
});

nativeAd.load();


// ...


// ... when native ad data is displayed on screen:
nativeAd.trackImpression();


// ...


// ... when native ad is clicked:
nativeAd.click(context);
````

````java

private void renderUiFromNativeAd(TapItNativeAd nativeAd) throws JSONException {
    JSONObject json = new JSONObject(nativeAd.getAdData());
    String adtitle = json.optString("adtitle");
    String imageurl = json.optString("iconurl");
    double stars = json.optDouble("rating");
    String html = json.optString("html");
    String adtext = json.optString("adtext");
    String cta = json.optString("cta");

    // use the data to build a view item of your own design...
}
````

To request multiple ads at once:
````java
String zoneId = "YOUR_NATIVE_AD_ZONE_ID";
TapItAdRequest request = TapItAdvertising.get().getAdRequestForZone(zoneId);

TapItAdvertising.get().getNativeAdLoader();
int numberOfAdsToLoad = 10;
TapItAdLoader<TapItNativeAd> adLoader = TapItAdvertising.get().getNativeAdLoader();

adLoader.multiLoad(context, request, numberOfAdsToLoad,
        new TapItAdLoader.TapItAdLoaderListener<TapItNativeAd>() {
            @Override
            public void onSuccess(TapItAdLoader adLoader, List<TapItNativeAd> nativeAdsList) {
                for(TapItNativeAd nativeAd : nativeAdsList) {
                    // use the native ad to build a view item, ...
                    try {
                        renderUiFromNativeAd(nativeAd);
                    } catch (JSONException e) {
                        // log error and discard this native ad instance
                    }
                }
            }

            @Override
            public void onFail(TapItAdLoader adLoader, String errMsg) {
                // no ads returned, errMsg describes why.
                // Error messages are not intended to be displayed to the user
            }
        }
);
````

Code samples and advanced implementation can be found in the 
[Native Ad Example Code](https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/nativeads-example/app/src/main/java/com/yourcompany/nativeadsexample/MainActivity.java)

Gradle project:
[Native Ad Example Project](https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/nativeads-example/)


Banner Usage
------------
Banners are inline ads that are shown alongside your apps interface.

*Xml Only Usage*
Add this in you layout xml:
````xml
<!-- Add banner to your layout xml -->
<!-- this will cause a 320x50 ad to be created, automatically kicking off ad rotation -->
<com.tapit.advertising.TapItBannerAdView
    android:id="@+id/bannerAd"
    android:layout_width="320dp"
    android:layout_height="50dp"
    zone="YOUR_ZONE_ID" />
````

*Example Code Usage*
Add this in you layout xml: (note that "zone" is not specified)
````xml
<!-- Add banner to your layout xml -->
<!-- this will cause a 320x50 ad to be created, automatically kicking off ad rotation -->
<com.tapit.advertising.TapItBannerAdView
    android:id="@+id/bannerAd"
    android:layout_width="320dp"
    android:layout_height="50dp" />
````

Add this to your activity:
````java
import com.tapit.advertising.*;

// ...

TapItBannerAdView bannerAdView = (TapItBannerAdView)findViewById(R.id.bannerAd);
bannerAdView.startRequestingAdsForZone("YOUR_BANNER_ZONE_ID");
````

Advanced implementation can be found in the [Example Code](https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/src/com/yourcompany/example/AdvertisingSample.java)


Interstitial Usage
------------------
Interstitials are best used at discrete stopping points in your app's flow, such as at the end of a game level, or when the player dies.

*Example Usage*
````java
import com.tapit.advertising.*;

// ...

TapItInterstitialAd interstitialAd = TapItAdvertising.get().getInterstitialAdForZone(this, "YOUR_INTERSTITIAL_ZONE_ID");
interstitialAd.show();
````

Advanced implementation can be found in the [Example Code](https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/src/com/yourcompany/example/AdvertisingSample.java)


Video Ads Usage
----------------
Video ads are interstitials that play a video.  They are best used at discrete
stopping points in your app's flow, such as at the end of a game level, or when the player dies.

*Example Usage*
````java
import com.tapit.advertising.*;

// ...

TapItVideoInterstitialAd videoAd = TapItAdvertising.get().getVideoInterstitialAdForZone(this, "YOUR_VIDEO_ZONE_ID");
videoAd.show();
````
Advanced implementation can be found in the [Example Code](https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/src/com/yourcompany/example/AdvertisingSample.java)
