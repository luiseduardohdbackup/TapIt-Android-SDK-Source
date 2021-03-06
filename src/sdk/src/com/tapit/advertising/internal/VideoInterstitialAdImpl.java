package com.tapit.advertising.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import com.tapit.advertising.*;
import com.tapit.core.TapItLog;
import com.tapit.vastsdk.*;
import com.tapit.vastsdk.player.TVASTPlayer;


public class VideoInterstitialAdImpl extends AbstractStatefulAd implements TapItVideoInterstitialAd, TVASTAdsLoader.TVASTAdsLoadedListener, TVASTAdErrorListener {

    private static final String TAG = "TapIt";

    private final TapItAdRequest adRequest;
    private final TVASTAdsLoader videoLoader;
    private TapItVideoInterstitialAdListener listener = null;
    private TVASTVideoAdsManager videoAdsManager = null;
    private DisplayMetrics metrics = null;
    private final String MIN_DISPLAY = "min_display";
    private int minDisplay = 0;

    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Video Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return An interstital ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static TapItVideoInterstitialAd getVideoInterstitialAdForZone(Context context, String zone) {
        TapItAdRequest request = new AdRequestImpl.BuilderImpl(zone).getTapItAdRequest();
        return VideoInterstitialAdImpl.getVideoInterstitialAd(context, request);

    }

    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return An interstital ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static TapItVideoInterstitialAd getVideoInterstitialAd(Context context, TapItAdRequest request) {
        return new VideoInterstitialAdImpl(context, request);
    }


    private VideoInterstitialAdImpl(Context context, TapItAdRequest request) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }

        if (request == null) {
            throw new NullPointerException("Ad request cannot be null");
        }

        setContext(context);
        adRequest = request;
        if(request.getCustomParameters()!=null && request.getCustomParameters().containsKey(MIN_DISPLAY)){
            try {
                minDisplay = Integer.parseInt(request.getCustomParameters().get(MIN_DISPLAY));
            } catch (NumberFormatException e) {
                TapItLog.e(TAG,"Error occurred while converting min_display value to String. "+minDisplay);
            }
        }
        metrics = context.getResources().getDisplayMetrics();

        videoLoader = new TVASTAdsLoader(context);
    }


    public VideoInterstitialAdImpl.TapItVideoInterstitialAdListener getListener() {
        return listener;
    }

    public void setListener(final VideoInterstitialAdImpl.TapItVideoInterstitialAdListener videoInterstitialListener) {
        this.listener = videoInterstitialListener;
    }

    public void doLoad() {
        //TODO delay showing until video is primed
        //TODO fire TapItVideoInterstitialAdListener callbacks

        TVASTAdsRequest tvastRequest = AdRequestImpl.asTVASTImplAdRequest(adRequest);
//        tvastRequest.initDefaultParameters(context);

        videoLoader.addAdsLoadedListener(this);
        videoLoader.addAdErrorListener(this);

        videoLoader.requestAds(tvastRequest);
    }

    public void doShow(final Context context) {

        AdActivityContentWrapper wrapper = new AdActivityContentWrapper() {

            private RelativeLayout layout = null;
            private VastPlayerView videoView = null;
            private TVASTAdView staticAdView = null;
            // stub code to enable forcing user to watch entire video
            private boolean canCloseVideo = false; // set to false to force user to watch entire video
            private TVASTPlayer.TVASTAdPlayerListener playerListener;

            @Override
            public View getContentView(final TapItAdActivity activity) {
                activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
                activity.getRequestedOrientation();
                activity.setCloseButtonVisible(canCloseVideo);
                activity.enableSystemUIAutoDimming();

                staticAdView = getAdView(activity);
                videoView = getVideoView(activity);

                layout = new RelativeLayout(activity);
                layout.addView(staticAdView);
                layout.addView(videoView);

                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP){
//                        TapItLog.d(TAG, "AdActivityContentWrapper -> videoView -> layout.onTouch");
                            canCloseVideo = true;
                            showAdClickDestination();
                        }

                        return true;
                    }
                });

                return layout;
            }

            private void showClosingFrameOrClose(TapItAdActivity activity) {
                layout.setOnClickListener(null);
                if (videoAdsManager.hasClosingFrame()) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) staticAdView.getLayoutParams();
                    lp.width = (int)(320 * metrics.density + 0.5);
                    lp.height = (int)(480 * metrics.density + 0.5);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
                    staticAdView.setLayoutParams(lp);
                    staticAdView.bringToFront();
                    staticAdView.requestLayout();
                    videoAdsManager.showClosingFrame(staticAdView);

                    // force orientation switch if img would overflow screen
                    int minHeight = Math.min(metrics.heightPixels, metrics.widthPixels);
                    if (minHeight < lp.height) {
                        // set to the orientation w/ the largest height
                        if (Build.VERSION.SDK_INT >= 18) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                        }
                        else {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    }
                    staticAdView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    canCloseVideo = true;
                }
                else {
                    activity.close();
                }
            }

            private void showAdClickDestination() {
                layout.setOnClickListener(null);
                if (videoAdsManager.hasDestinationUrl()) {
                    videoView.stopAd();
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) staticAdView.getLayoutParams();
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    staticAdView.setLayoutParams(lp);
                    staticAdView.requestLayout();
                    videoAdsManager.loadDestinationUrl(staticAdView);
                    videoAdsManager.triggerPostBacks();
                    staticAdView.bringToFront();
                    staticAdView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                }
            }

            private TVASTAdView getAdView(final TapItAdActivity activity) {
                TVASTAdView adView = new TVASTAdView(activity);
                adView.setAdViewListener(new TVASTAdView.AdViewListener() {
                    @Override
                    public void onLoaded(TVASTAdView adView) {
                        // noop
                    }

                    @Override
                    public void onError(TVASTAdView adView, String error) {
                        // noop
                    }

                    @Override
                    public void onClicked(TVASTAdView adview) {
                        // noop
                    }

                    @Override
                    public void willLeaveApplication(TVASTAdView adview) {
                        if (listener != null) {
                            listener.videoInterstitialActionWillLeaveApplication(VideoInterstitialAdImpl.this);
                        }
                    }
                });
                adView.setVisibility(View.GONE);
                return adView;
            }

            private VastPlayerView getVideoView(final TapItAdActivity activity) {
                videoView = new VastPlayerView(activity);

                playerListener = new TVASTPlayer.TVASTAdPlayerListener() {
                    @Override
                    public void onVideoClick(TVASTPlayer player) {
                        TapItLog.d(TAG, "onVideoClick");
                    }

                    @Override
                    public void onVideoComplete(TVASTPlayer player) {
                        TapItLog.d(TAG, "Video Ad Complete!");
                        canCloseVideo = true;

                        activity.setCloseButtonVisible(true);
                        showClosingFrameOrClose(activity);
                    }

                    @Override
                    public void onVideoError(TVASTPlayer player) {
                        if (listener != null) {
                            //TODO pass a more useful message?
                            String msg = "Failure during video playback";
                            listener.videoInterstitialDidFail(VideoInterstitialAdImpl.this, msg);
                        }
                    }

                    @Override
                    public void onVideoPause(TVASTPlayer player) {
                        // noop
                    }

                    @Override
                    public void onVideoPlay(TVASTPlayer player) {
                        // noop
                    }

                    @Override
                    public void onVideoProgress(TVASTPlayer player, int current, int max) {
                        //This is to support a hidden feature for BrandMe. So they can force user to watch an ad for
                        // given amount of seconds before the close button appears.
                        minDisplay = minDisplay < 0 ? max : minDisplay;
                        if(current >= minDisplay) {
                            canCloseVideo = true;
                            activity.setCloseButtonVisible(true);
                        }
                    }

                    @Override
                    public void onVideoResume(TVASTPlayer player) {
                        // noop
                    }

                    @Override
                    public void onVideoVolumeChanged(TVASTPlayer player, int volume) {
                        // noop
                    }
                };

                videoView.addCallback(playerListener);

                return videoView;
            }

            @Override
            public void done() {
                TapItLog.d(TAG, "done called!");
                if (listener != null) {
                    listener.videoInterstitialDidClose(VideoInterstitialAdImpl.this);
                }
                ratchetState(State.DONE);
                staticAdView.destroy();
                layout.removeAllViews();
            }

            @Override
            public boolean shouldClose() {
                TapItLog.d(TAG, "shouldClose Called!");
                return canCloseVideo;
            }

            @Override
            public void startContent() {
                TapItLog.d(TAG, "startContent");
                videoAdsManager.play(videoView);
            }

            @Override
            public void stopContent() {
                videoView.stopAd();
                TapItLog.d(TAG, "stopContent");
            }
        };
        Intent i = new Intent(context, TapItAdActivity.class);
        Parcelable wrapperSharable = new Sharable<AdActivityContentWrapper>(wrapper, TapItAdActivity.CONTENT_WRAPPER_EXTRA);
        i.putExtra(TapItAdActivity.CONTENT_WRAPPER_EXTRA, wrapperSharable);
        context.startActivity(i);
    }

    @Override
    public void onAdError(TVASTAdErrorEvent adErrorEvent) {
        TapItLog.d(TAG, "Ad error: " + adErrorEvent);
        videoLoader.removeAdErrorListener(this);
        videoLoader.removeAdsLoadedListener(this);

        if (listener != null) {
            listener.videoInterstitialDidFail(this, adErrorEvent.getError().getMessage());
        }
        ratchetState(State.DONE);
    }

    @Override
    public void onAdsLoaded(TVASTAdsLoader.TVASTAdsLoadedEvent event) {
        TapItLog.d(TAG, "Ad Loaded: " + event);
        videoLoader.removeAdErrorListener(this);
        videoLoader.removeAdsLoadedListener(this);

        videoAdsManager = event.getManager();
        videoAdsManager.addAdEventListener(new TVASTVideoAdsManager.TVASTAdEventListener() {
            @Override
            public void onAdEvent(TVASTVideoAdsManager.TVASTAdEvent adEvent) {
                TapItLog.d(TAG, "videoAdsManager.onAdEvent: " + adEvent.getEventType());
            }
        });
        videoAdsManager.showCloseButton(false);
        if (ratchetState(State.LOADED)) {
            if (showImmediately) {
                show();
            }
        }

        if (listener != null) {
            listener.videoInterstitialDidLoad(this);
        }
    }
}
