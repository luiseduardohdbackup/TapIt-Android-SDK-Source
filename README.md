TapIt Android SDK
=================

Version 1.7.9

This is the Android SDK for the TapIt! mobile ad network. Go to http://tapit.com/ for more details and to sign up.

``/dist`` Library files to be included in your app<br/>
``/src`` SDK and example project source


Usage:
------
To install, extract the SDK archive(https://github.com/tapit/TapIt-Android-SDK-Source/raw/master/dist/TapItSDK-android.zip) into your project's ```/lib``` folder, and add TapItAdView.jar into the project's build path:

![Add TapItAdView.jar to Build Path](https://raw.github.com/tapit/TapIt-Android-SDK-Source/master/doc/assets/add_jar.png)

Set TapItAdView.jar to be exported as part of your apk file:

![Export TapItAdView.jar](https://raw.github.com/tapit/TapIt-Android-SDK-Source/master/doc/assets/export_jar.png)


````xml
<uses-permission android:name="android.permission.INTERNET"></uses-permission>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>

<!-- Optional permissions to enable ad geotargeting
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
-->

<!-- inside of the application tag: -->
<activity
    android:name="com.tapit.adview.AdActivity"
    android:configChanges="keyboard|keyboardHidden|orientation" />

````
Your manifest should look something like this:

![Example manifest](https://raw.github.com/tapit/TapIt-Android-SDK-Source/master/doc/assets/manifest.png)

See https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/AndroidManifest.xml for an example manifest file.

**NOTE:** Zones correspond to a specific ad type, which is specified through the TapIt dashboard.  Please ensure that you use the correct Zone ID for your ad units or you may experience un-expected results.

A sample project is included in this repo.  See https://github.com/tapit/TapIt-Android-SDK-Source/tree/master/src/example for a live demo.

AdPrompt Usage
--------------
AdPrompts are a simple ad unit designed to have a native feel. The user is given the option to download an app, and if they accept, they are taken to the app within the app marketplace.

````java
AdPrompt adPrompt = new AdPrompt(this, "YOUR_ZONE_ID");
adPrompt.showAdPrompt();
````

Sample implementation can be found here: https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/src/com/yourcompany/TapItTestActivity.java#L178


Banner Usage
------------
````xml
<!-- Add banner to your layout xml -->
<!-- this will center a 320x50 ad at the bottom of the screen, -->
<!-- assuming a RelativeLayout is used -->
<com.tapit.adview.AdView
    android:id="@+id/bannerAd"
    android:layout_width="320dp"
    android:layout_height="50dp"
    android:layout_alignParentBottom="true"
    android:layout_centerHorizontal="true"
    zone="YOUR_ZONE_ID" />
````

````java
import com.tapit.adview.AdView;
// ...
private AdView bannerAd;
// ...
bannerAd = (AdView)findViewById(R.id.bannerAd);

// you can optionally register a listener to be notified of banner lifecyle events:
bannerAd.setOnAdDownload(new OnAdDownload() {
    // anonymous listener

    public void begin(AdViewCore adView) {
        // fired before banner download begins.
    }

    public void end(AdViewCore adView) {
        // fired after banner content fully downloaded.
    }

    public void error(AdViewCore adView, String error) {
        // fired after fail to download content.
    }

    public void clicked(AdViewCore adView) {
        // fired after a user taps the ad.
    }

    public void willPresentFullScreen(AdViewCore adView) {
        // fired just before an ad takes over the screen.
    }

    public void didPresentFullScreen(AdViewCore adView) {
        // fired once an ad takes over the screen.
        // Stop updating your UI to allow for a smooth ad experience
    }

    public void willDismissFullScreen(AdViewCore adView) {
        // fired just before an ad dismisses it's full screen view.
    }

    public void willLeaveApplication(AdViewCore adView) {
        // fired just before the app will be sent to the background.
    }
});
````

Sample implementation can be found here: https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/src/com/yourcompany/TapItTestActivity.java#L59


Interstitial Usage
------------------
Interstitials are best used at discrete stopping points in your app's flow, such as at the end of a game level, or when the player dies.

*Simple Implementation*
````java
AdInterstitialView interstitialAd; // class property

...

interstitialAd = new AdInterstitialView(this, "YOUR_ZONE_ID");
interstitialAd.load(); // request an ad from the server, to be displayed later...

... // some time passes as your app continues normally

// when you're ready to display interstitial,
if(interstitialAd.isLoaded()) {
    // interstitial was loaded successfully, show it!
    interstitialAd.showInterstitial();
}
````

*Advanced Implementation*

For more control of the interstitial lifecycle, listen for ``OnInterstitialAdDownload`` events
````java
AdInterstitialView interstitialAd; // class property

...

interstitialAd = new AdInterstitialView(this, "YOUR_ZONE_ID");
// register a listener to be notified on interstitial state changes
interstitialAd.setOnInterstitialAdDownload(new OnInterstitialAdDownload() {
    // anonymous listener

    public void willLoad(AdViewCore adView) {
        // fired before banner download begins.
    }

    public void ready(AdViewCore adView) {
        // fired after banner content is fully downloaded.
        // you can show the interstitial at any time after receiving this event.

        // in this example, we show the interstitial as soon as it's ready
        interstitialAd.showInterstitial();
    }

    public void willOpen(AdViewCore adView) {
        // fired just before an action is fired.
        // Stop updating your UI to allow for a smooth ad experience
    }

    public void didClose(AdViewCore adView) {
        // fired after an interstitial closes and your app is again visible.
        // Start updating your UI again.
    }

    public void error(AdViewCore adView, String error) {
        // fired if the interstitial request fails to return an ad.
        // Throw this interstitial object away.
    }

    public void clicked(AdViewCore adView) {
        // fired after a user taps the ad.
    }

    public void willLeaveApplication(AdViewCore adView) {
        // fired just before the app will be sent to the background.
    }
});
interstitialAd.load(); // request an ad from the server, to be displayed later...
````

Sample implementation can be found here: https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/src/com/yourcompany/TapItTestActivity.java#L191


Video Ads Usage
----------------
TVAST SDK Version 1.0.3

For sample video ads integration code, please see the VideoAdActivity.java and supporting VideoFullScreenActivity.java files for working example of video ads in an app.  You can find the above mentioned files in the src directory under com.tvast.tvastsdktest folder in this project.

The TapItVAST SDK is resides in the included tapitvastsdk.jar file in the "lib" folder of this project.  The tapitvastsdk.jar file should be copied to the libs folder of any project that wishes to offer videos ads using the TapItVAST SDK.
 
Essentially, what needs to be included in the code are as follows:

  public final static String VIDEO_ZONE_ID = "22219";
  public final static String VIDEO_TYPE = "pre-roll";

  android.util.Log.d("TapItVASTSDKTest", "TapItVAST SDK Version: " + TVASTAd.VERSION);

  mVideoPlayer = (DemoPlayer) findViewById(R.id.contentVideo);
  mAdPlayer = (DemoPlayer) findViewById(R.id.adVideo);

  mAdsLoader = new TVASTAdsLoader(this);
  mAdsLoader.addAdErrorListener(this);
  mAdsLoader.addAdsLoadedListener(this);

  setButtonListeners();
  mVideoPlayer.addCallback(this);
  mAdPlayer.addCallback(this);


  protected void requestAd() {
    TVASTAdsRequest request = new TVASTAdsRequest(VIDEO_ZONE_ID);
    request.setRequestParameter("videotype", VIDEO_TYPE);
    mAdsLoader.requestAds(request);
  }

  @Override
  public void onAdError(TVASTAdErrorEvent event) {
    toConsole("Ads error: " + event.getError().getMessage() + "\n");
    mResetAdButton.setEnabled(true);
    mRequestAdButton.setEnabled(true);
  }

  @Override
  public void onAdsLoaded(TVASTAdsLoadedEvent event) {
    toConsole("Ads loaded!");
    mAdsManager = (TVASTVideoAdsManager) event.getManager();
    mAdsManager.addAdErrorListener(this);
    mAdsManager.addAdEventListener(this);
    mResetAdButton.setEnabled(true);
    mRequestAdButton.setEnabled(true);

    mAdsManager.setIsFullscreen(mFullscreenAdCheckBox.isChecked());
    if (mAdsManager.isFullscreen()) {
		Intent intent = new Intent(this, VideoFullScreenActivity.class);
		TVASTSharable sharedAdsManager = new TVASTSharable(mAdsManager);
		intent.putExtra(VideoFullScreenActivity.EXTRA_AD_MANAGER, sharedAdsManager);
		startActivity(intent);
    }		
    else
    	mAdsManager.play(mAdPlayer);
  }

  @Override
  public void onAdEvent(TVASTAdEvent event) {
    toConsole("Event:" + event.getEventType());

    switch (event.getEventType()) {
      case CONTENT_PAUSE_REQUESTED:
        if (mContentStarted) {
        	mVideoPlayer.pauseContent();
        	mVideoPlayer.getVideoView().setVisibility(View.GONE);
        	mAdPlayer.getVideoView().setVisibility(View.VISIBLE);
        	mAdPlayer.bringToFront();
        	mAdPlaying = true;
        }
        break;
      case CONTENT_RESUME_REQUESTED:
        if (mContentStarted) {
        	mVideoPlayer.resumeContent();
        	mVideoPlayer.getVideoView().setVisibility(View.VISIBLE);
        	mVideoPlayer.bringToFront();
        	mAdPlayer.getVideoView().setVisibility(View.GONE);
        	mAdPlaying = false;
        }
        break;
      case CLICK:
        mAdsManager.loadDestinationUrl(mWebView);
    	break;
    }
  }


ProGuard Settings
-----------------
Recommended ProGuard settings can be found here:
https://github.com/tapit/TapIt-Android-SDK-Source/blob/master/src/example/proguard-project.txt
