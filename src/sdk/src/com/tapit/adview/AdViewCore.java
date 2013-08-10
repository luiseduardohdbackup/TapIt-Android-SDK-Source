package com.tapit.adview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;


/**
 * Viewer of advertising.
 */
public abstract class AdViewCore extends WebView {
    public static final String VERSION = "2.0.0";
    public static final String TAG = "AdViewCore";

    private static final long AD_DEFAULT_RELOAD_PERIOD = 120000; // milliseconds
//    private static final long AD_STOP_CHECK_PERIOD = 10000; // milliseconds
    Handler handler = new Handler(Looper.getMainLooper());
    protected Context context;
    private Integer defaultImageResource;
    private Timer reloadTimer;
    protected AdRequest adRequest;
    private OnAdClickListener adClickListener;
    private long adReloadPeriod = AD_DEFAULT_RELOAD_PERIOD;
    private OnAdDownload adDownload;

    protected float mDensity; // screen pixel density
//    private int mDefaultHeight; // default height of the view
//    private int mDefaultWidth; // default width of the view
    private int mInitLayoutHeight; // initial height of the view
    private int mInitLayoutWidth; // initial height of the view
    private int adHeight = -1; // ad height, as reported by server
    private int adWidth = -1; // ad width, as reported by server
    private int mIndex; // index of the view within its viewgroup

    private String mClickURL;
        
    private boolean bGotLayoutParams;
        
    private boolean isBannerAnimationEnabled = true;
    private boolean animateBack = false;
    private boolean isFirstTime = true;
        
    private boolean openInInternalBrowser = true;
        
//    private static final int MESSAGE_RESIZE = 1000;
//    private static final int MESSAGE_CLOSE = 1001;
//    private static final int MESSAGE_HIDE = 1002;
//    private static final int MESSAGE_SHOW = 1003;
//    private static final int MESSAGE_EXPAND = 1004;
//    private static final int MESSAGE_SEND_EXPAND_CLOSE = 1005;
//    private static final int MESSAGE_OPEN = 1006;
//    private static final int MESSAGE_PLAY_VIDEO = 1007;
//    private static final int MESSAGE_PLAY_AUDIO = 1008;
//    private static final int MESSAGE_RAISE_ERROR = 1009;

    public static final String DIMENSIONS = "expand_dimensions";
    public static final String PLAYER_PROPERTIES = "player_properties";
    public static final String EXPAND_URL = "expand_url";
    public static final String ACTION_KEY = "action";
//    private static final String EXPAND_PROPERTIES = "expand_properties";
//    private static final String RESIZE_WIDTH = "resize_width";
//    private static final String RESIZE_HEIGHT = "resize_height";

//    private static final String ERROR_MESSAGE = "message";
//    private static final String ERROR_ACTION = "action";

    protected static final int BACKGROUND_ID = 101;
    protected static final int PLACEHOLDER_ID = 100;
    protected boolean mraid = false;
    private MraidState mraidState = MraidState.LOADING;
    private MraidPlacementType mraidPlacementType = MraidPlacementType.INLINE;

    public MraidPlacementType getMraidPlacementType() {
        return mraidPlacementType;
    }

    public void setMraidPlacementType(MraidPlacementType mraidPlacementType) {
        this.mraidPlacementType = mraidPlacementType;
    }

    private enum ViewState {
        DEFAULT, RESIZED, EXPANDED, HIDDEN
    }

    private ViewState mViewState = ViewState.DEFAULT;
    public String mDataToInject = null;
    public static final String mraidBridgePath = "http://dev.tapit.com/~npenteado/mraid/mraid.js";
    private String mContent;

    protected AdLog adLog = new AdLog(this);

    private String typeOfBanner = TYPE_BANNER;
        
    private static final String TYPE_BANNER = "banner";
    private static final String TYPE_OFFERWALL = "offerwall";

    public Object lock = new Object();

    private LoadContentTask contentTask;
        
    private Integer backgroundColor = Color.TRANSPARENT; 

    public enum ACTION {
        PLAY_AUDIO, PLAY_VIDEO
    }


    private FrameLayout placeholderView = null;


    /**
     * Creation of viewer of advertising.
     * 
     * @param context
     *            - The reference to the context of Activity.
     * @param zone
     *            - The id of the zone of publisher site.
     */
    public AdViewCore(Context context, String zone) {
        super(context);
        loadContent(context,
                null, null, null, null,
                null,
                zone, null, null,
                null, null, null, null, null);
    }

    /**
     * Creation of viewer of advertising. It is used for element creation in a
     * XML template.
     */
    public AdViewCore(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    /**
     * Creation of viewer of advertising. It is used for element creation in a
     * XML template.
     */
    public AdViewCore(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    /**
     * Creation of viewer of advertising. It is used for element creation in a
     * XML template.
     */
    public AdViewCore(Context context) {
        super(context);
        initialize(context, null);
    }

    /**
     * Get interface for advertising opening.
     */
    public OnAdClickListener getOnAdClickListener() {
        return adClickListener;
    }

    /**
     * Set interface for advertising opening.
     */
    public void setOnAdClickListener(OnAdClickListener adClickListener) {
        this.adClickListener = adClickListener;
    }

    /**
     * The interface for advertising opening in an internal browser.
     */
    public interface OnAdClickListener {
        public void click(String url);
    }

    /**
     * The interface for advertising downloading.
     */
    public interface OnAdDownload {
        /**
         * This event is fired before banner download begins.
         */
        public void begin(AdViewCore adView);

        /**
         * This event is fired after banner content fully downloaded.
         */
        public void end(AdViewCore adView);

        /**
         * This event is fired after fail to download content.
         */
        public void error(AdViewCore adView, String error);
                
        /**
         * This event is fired after a user taps the ad.
         */
        public void clicked(AdViewCore adView);

        /**
         * This event is fired just before an ad takes over the screen.
         */
        public void willPresentFullScreen(AdViewCore adView);
                
        /**
         * This event is fired once an ad takes over the screen.
         */
        public void didPresentFullScreen(AdViewCore adView);
                
        /**
         * This even is fired just before an ad dismisses it's full screen view.
         */
        public void willDismissFullScreen(AdViewCore adView);
                
        /**
         * This event is fired just before the app will be sent to the background.
         */
        public void willLeaveApplication(AdViewCore adView);
    }

    /**
     * The interface for advertising downloading.
     */
    public interface OnInterstitialAdDownload {
        /**
         * This event is fired before banner download begins.
         */
        public void willLoad(AdViewCore adView);

        /**
         * This event is fired after banner content is fully downloaded.
         */
        public void ready(AdViewCore adView);
                
        /**
         * This event is fired just before an action is fired.
         */
        public void willOpen(AdViewCore adView);
                
        /**
         * This event is fired after an interstitial closes and your app is again visible.
         */
        public void didClose(AdViewCore adView);
                
        /**
         * This event is fired if the interstitial request fails to return an ad.
         */
        public void error(AdViewCore adView, String error);

        /**
         * This event is fired after a user taps the ad.
         */
        public void clicked(AdViewCore adView);

        /**
         * This event is fired just before the app will be sent to the background.
         */
        public void willLeaveApplication(AdViewCore adView);
    }

    /**
     * Get interface for advertising downloading.
     */
    public OnAdDownload getOnAdDownload() {
        return adDownload;
    }

    /**
     * Set interface for advertising downloading.
     */
    public void setOnAdDownload(OnAdDownload adDownload) {
        this.adDownload = adDownload;
    }

    /**
     * Optional. Get Custom Parameters.
     */
    public Map<String, String> getCustomParameters() {
        if (adRequest != null) {
            return adRequest.getCustomParameters();
        } else {
            return null;
        }
    }

    /**
     * @deprecated Use setCustomParameters(Map<String, String> customParameters) instead
     * Optional. Set Custom Parameters.
     */
    public void setCustomParameters(Hashtable<String, String> customParameters) {
        if (adRequest != null) {
            adRequest.setCustomParameters(customParameters);
        }
    }

    /**
     * Optional. Set Custom Parameters.
     */
    public void setCustomParameters(Map<String, String> customParameters) {
        if (adRequest != null) {
            adRequest.setCustomParameters(customParameters);
        }
    }

    /**
     * Optional. Get image resource which will be shown during advertising
     * loading if there is no advertising in a cache.
     */
    public Integer getDefaultImage() {
        return defaultImageResource;
    }

    /**
     * Optional. Set image resource which will be shown during advertising
     * loading if there is no advertising in a cache.
     */
    public void setDefaultImage(Integer defaultImage) {
        defaultImageResource = defaultImage;
    }

    /**
     * Optional. Get banner refresh interval (in seconds).
     */
    public int getUpdateTime() {
        return (int) (adReloadPeriod / 1000);
    }

    /**
     * Optional. Set banner refresh interval (in seconds).
     */
    public void setUpdateTime(int updateTime) {
        boolean b = adReloadPeriod == 0; 
        this.adReloadPeriod = updateTime * 1000; // milliseconds
        if (b) {
            scheduleUpdate();
        }
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            String zone = Utils.getStringResource(context, attrs.getAttributeValue(null, "zone"));
            String keywords = Utils.getStringResource(context, attrs.getAttributeValue(null, "keywords"));
            String latitude = Utils.getStringResource(context, attrs.getAttributeValue(null, "latitude"));
            String longitude = Utils.getStringResource(context, attrs.getAttributeValue(null, "longitude"));
            String ua = Utils.getStringResource(context, attrs.getAttributeValue(null, "ua"));
            String paramBG = Utils.getStringResource(context, attrs.getAttributeValue(null, "paramBG"));
            String paramLINK = Utils.getStringResource(context, attrs.getAttributeValue(null, "paramLINK"));
            Integer defaultImage = attrs.getAttributeResourceValue(null, "defaultImage", -1);
            Long p = getLongParameter(attrs.getAttributeValue(null, "adReloadPeriod"));
            if (p != null)
                this.adReloadPeriod = p; 
            Integer minSizeX = getIntParameter(attrs.getAttributeValue(null, "minSizeX"));
            Integer minSizeY = getIntParameter(attrs.getAttributeValue(null, "minSizeY"));
            Integer sizeX = getIntParameter(attrs.getAttributeValue(null, "sizeX"));
            Integer sizeY = getIntParameter(attrs.getAttributeValue(null, "sizeY"));
//            String background = attrs.getAttributeValue("android", "background");
                        
            loadContent(context,
                    minSizeX, minSizeY, sizeX, sizeY,
                    defaultImage,
                    zone, keywords, latitude,
                    longitude, ua, paramBG, paramLINK,
                    null);
        }
    }

    private void loadContent(Context context,
            Integer minSizeX, Integer minSizeY, Integer sizeX, Integer sizeY,
            Integer defaultImage,
            String zone,
            String keywords, String latitude,
            String longitude, String ua,
            String paramBG, String paramLINK,
            Map<String, String> customParameters) {
        this.context = context;
        adRequest = new AdRequest(adLog);
        adRequest.initDefaultParameters(context);
        adRequest
                .setUa(ua)
                .setZone(zone)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setParamBG(paramBG)
                .setParamLINK(paramLINK)
//                .setMinSizeX(minSizeX)
//                .setMinSizeY(minSizeY)
//                .setSizeX(sizeX)
//                .setSizeY(sizeY)
                .setCustomParameters(customParameters);

        defaultImageResource = defaultImage;

        WebSettings webSettings = getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                
        setScrollContainer(false);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
                
        setWebViewClient(new AdWebViewClient(context));
        setWebChromeClient(mWebChromeClient);
    }
        
    /**
     * Gets the size.
     * 
     * @return the size
     */
    public String getSize() {
        return "{ width: " + (int) (getWidth() / mDensity) + ", " + "height: "
                + (int) (getHeight() / mDensity) + "}";
    }

    @Override
    protected void onAttachedToWindow() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (!bGotLayoutParams && lp != null) {
            mInitLayoutHeight = lp.height;
            mInitLayoutWidth = lp.width;
            bGotLayoutParams = true;
        }

        if (isFirstTime){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "onAttachedToWindow", "isFirstTime is true. Called LoadContentTask");
                    update(false);
                }
            });
        }
                
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
        
    @Override
    public void destroy() {
        cancelUpdating();
        super.destroy();
    }
        
    protected void cancelUpdating(){
        adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "cancelUpdating", "cancelUpdating called");
        removeAllViews();
        if (reloadTimer != null) {
            try {
                reloadTimer.cancel();
                reloadTimer = null;
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "cancelUpdating",
                        e.getMessage());
            }
        }

        if (contentTask != null) {
            try {
                contentTask.cancel(true);
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "cancelUpdating",
                        e.getMessage());
            }
        }
        setUpdateTime(0);
    }

    /**
     * Immediately update banner contents.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public void update(boolean forced) {
        contentTask = new LoadContentTask((AdView)this, forced);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            contentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
        else
            contentTask.execute(0);
    }


    private class LoadContentTask extends AsyncTask<Integer, Integer, String>{
                
        private static final int BEGIN_STATE = 0;
        private static final int END_STATE = 1;
        private static final int ERROR_STATE = 2;

        private boolean forced;
        private AdView view;
        private String error;
        private String requestUrl;
                
        public LoadContentTask(AdView view, boolean forced) {
            this.forced = forced;
            this.view = view;
        }

        @Override
        protected String doInBackground(Integer... params) {
            String retData = null;
            setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            synchronized (lock) {
                adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, TAG, "LoadContentTask started");
                        
                boolean fTime = isFirstTime;                
                boolean doProcess = isShown() || forced;
        
                if (!doProcess || mViewState != ViewState.DEFAULT || adRequest == null) {
                    scheduleUpdate();
                    return null;
                }
                                
                isFirstTime = false;
                                
                if (fTime) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (defaultImageResource != null && defaultImageResource > 0) {
                                view.setBackgroundResource(defaultImageResource);
                            }
                        }
                    });
                }
                                
                /*
                 * "type":"video"
                 * seek: "videourl" & "clickurl"
                 * 
                 * "type":"offerwall"
                 * etc.
                 * 
                 * "type":"banner"
                 * seek: "html"
                 * 
                 * "type":"ormma"
                 * seek...
                 */
                String data = null;
//                String videourl = null;
                String clickurl = null;
                // calc request size based on size of layout allotted
                view.calcDimensionsForRequest(context);

                final String url = adRequest.createURL();
                requestUrl = url;
                boolean forceMraid = false;
                try {
                    publishProgress(BEGIN_STATE);

                    Log.d("TapIt", url);
                    data = requestGet(url);
                    Log.d("TapIt", data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if(jsonObject.has("error")) {
                            // failed to retrieve an ad, abort and call the error callback
                            data = null;
                            throw new Exception(jsonObject.getString("error"));
                        }
                        else if (jsonObject.has("type")){
                            if (jsonObject.has("mraid")) {
                                forceMraid = jsonObject.getBoolean("mraid");
                            }
                            typeOfBanner = jsonObject.getString("type");
                            if (TYPE_BANNER.equals(typeOfBanner)){
                                data = (String) jsonObject.get("html");
                            } else if (TYPE_OFFERWALL.equals(typeOfBanner)){
                                // do nothing
                            } else {// if nothing equal then assume this is BANNER
                                typeOfBanner = TYPE_BANNER;
                                data = (String) jsonObject.get("html");
                                if("".equals(data)) {
                                    this.error = "server returned a blank ad";
                                    publishProgress(ERROR_STATE);
                                }
                            }
                                                
                            if(jsonObject.has("clickurl")) {
                                clickurl = jsonObject.getString("clickurl");
                                mClickURL = clickurl;
                            }


                            if(jsonObject.has("adHeight")) {
                                try {
                                    adHeight = Integer.parseInt(jsonObject.getString("adHeight"));
                                } catch(NumberFormatException e) {
                                    adHeight = -1;
                                }
                            }
                            else {
                                adHeight = -1;
                            }
                            if(jsonObject.has("adWidth")) {
                                try {
                                    adWidth = Integer.parseInt(jsonObject.getString("adWidth"));
                                } catch(NumberFormatException e) {
                                    adWidth = -1;
                                }
                            }
                            else {
                                adWidth = -1;
                            }

                            if (jsonObject.has("mraid")) {
                            }
                        }
                                        
                        publishProgress(END_STATE);
                    } catch (JSONException e) {
                        if("".equals(data)) {
                            this.error = "server returned an empty response";
                            publishProgress(ERROR_STATE);
                        }
                        else {
                            adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR,
                                    "Load JSON", e.getMessage());
                            // not json output, assume html
                            typeOfBanner = TYPE_BANNER;
                            publishProgress(END_STATE);
                        }
                    }
                } catch (Exception e) {
                    adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR,
                            "contentThreadAction.requestGet", e.getMessage());
                    this.error = e.getMessage();
                    publishProgress(ERROR_STATE);
                }
                try{
                    if ((data != null) && (data.length() > 0)) {
                        handler.post(new RemoveAllChildViews(view)); // ???
                
//                        data = wrapToHTML(data, mBridgeScriptPath, mScriptPath);
//                        mContent = data;
                                                
//                        view.clearCache(true);
                
                        if (TYPE_BANNER.equals(typeOfBanner)){
                            mraid = forceMraid || checkIfMraid(data);
                            data = wrapToHTML(data, mraid);
                            mContent = data;
                                                        
                            if (isBannerAnimationEnabled && !fTime){
                                final float centerX = getWidth() / 2.0f;
                                final float centerY = getHeight() / 2.0f;
                        
                                // Create a new 3D rotation with the supplied parameter
                                // The animation listener is used to trigger the next animation
                                final Rotate3dAnimation rotation =
                                        new Rotate3dAnimation(0, 90, centerX, centerY, 310.0f, true);
                                rotation.setDuration(500);
                                rotation.setFillAfter(true);
                                rotation.setInterpolator(new AccelerateInterpolator());
                                rotation.setAnimationListener(new AnimationListener() {
                                                                        
                                    @Override
                                    public void onAnimationStart(Animation animation) {}
                                                                        
                                    @Override
                                    public void onAnimationRepeat(Animation animation) {}
                                                                        
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        loadDataWithBaseURL(url, mContent);
                                    }
                                });
                                handler.post(new Runnable() {
                                                                        
                                    @Override
                                    public void run() {
                                        clearAnimation();
                                        startAnimation(rotation);
                                        animateBack = true;
                                    }
                                });
                            } else {
                                retData = data;
//                                loadDataWithBaseURL(data);
                            }
//                        } else if (TYPE_VIDEO.equals(typeOfBanner)) {
////                            Dimensions d = null;
////                            d = new Dimensions();
////                            d.x = 0; d.y = 0; d.width = 480; d.height = 480;
////                            Log.d(TAG, videourl);
//                            boolean audioMuted = false;
//                            boolean autoPlay = false;
//                            boolean showControls = false;
//                            boolean repeat = false;
//                            playVideo(videourl, clickurl, audioMuted, autoPlay, showControls, repeat, null, "fullscreen", "exit");
                        } else if (TYPE_OFFERWALL.equals(typeOfBanner)) {
                            data = wrapToHTML(data, false);
                            mContent = data;
                            retData = data;
//                            loadDataWithBaseURL(data);
                        }
                    } else {
                        interstitialClose();
                    }
                } catch (Exception e) {
                    adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "LoadContentTask",
                        e.getMessage());
                    Log.e("TapIt", "An error occured", e);
                }
                scheduleUpdate();
                                
                return retData;
            }
        }
                
        @Override
        protected void onProgressUpdate (Integer... values) {
            int state = values[0];

            if (adDownload != null) {
                switch(state) {
                case BEGIN_STATE:
                    adDownload.begin((AdViewCore)this.view);
                    break;
                case END_STATE:
                    adDownload.end((AdViewCore)this.view);
                    break;
                case ERROR_STATE:
                    adDownload.error((AdViewCore)this.view, this.error);
                    break;
                }
            }
        }
                
        @Override
        protected void onPostExecute(String htmlData) {
            if(htmlData != null) {
                loadDataWithBaseURL(requestUrl, htmlData);
            }
        }
                
    }
        
    protected String wrapToHTML(String data, boolean isMraid){
        String alignment;
        if(adWidth > 0) {
            alignment = "style=\"width:" + adWidth + "px; margin:0 auto; text-align:center;\"";
        }
        else {
            alignment = "align=\"left\"";
        }

        String mraidTag = "";
        if (isMraid) {
            mraidTag = "<script type=\"text/javascript\">" +
                    MraidJS.MRAID_JS +
                    "</script>";
//          mraidTag += "<script src=\"" + mraidBridgePath + "\"></script>";
        }
        return "<html><head>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />"
            + "<title>Advertisement</title> "
            + mraidTag
            + "</head>"
            + "<body style=\"margin:0; padding:0; overflow:hidden; background-color:transparent;\">"
            + "<div " + alignment + ">"
            + data 
            + "</div> "
            + "</body> "
            + "</html> ";
    }

    protected void interstitialClose() {

    }

    private TimerTask lastTimerTask = null;
        
    private void scheduleUpdate(){
        adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, TAG, "scheduleUpdate " + adReloadPeriod);
        if (lastTimerTask != null)
            lastTimerTask.cancel();
        lastTimerTask = new TimerTask() {

            @Override
            public void run() {
                update(false);
            }
        };
                
        if (adReloadPeriod > 0){
            if (reloadTimer == null)
                reloadTimer = new Timer(true);
            reloadTimer.schedule(lastTimerTask, adReloadPeriod);
        } else {
            if (reloadTimer != null)
                reloadTimer.cancel();
            reloadTimer = null;
        }
    }
        
        
    private class RemoveAllChildViews implements Runnable {
        private ViewGroup view;

        public RemoveAllChildViews(ViewGroup view) {
            this.view = view;
        }

        @Override
        public void run() {
            try {
                view.removeAllViews();
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "RemoveAllChildViews",
                        e.getMessage());
            }
        }
    }

    private class AdWebViewClient extends WebViewClient {
        private Context context;
        int numPagesLoading = 0;

        public AdWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if(url.startsWith("nativecall://")) {
                  handleNativeMraidCall(url);
                  return true;
                }

                adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "OverrideUrlLoading", url);
                if (adClickListener != null) {
                    adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "OverrideUrlLoading - click", url);
                    adClickListener.click(url);
                } else {
                    int isAccessNetworkState = context
                            .checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);

                    if(adDownload != null) {
                        adDownload.clicked((AdViewCore)view);
                    }
                    if (isAccessNetworkState == PackageManager.PERMISSION_GRANTED) {
                        if (isInternetAvailable(context)) {
                            adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "OverrideUrlLoading - openUrlInExternalBrowser", url);
                            if(adDownload != null) {
                                adDownload.willPresentFullScreen((AdViewCore)view);
                            }
                            openUrlInExternalBrowser(context, url);
                            if(adDownload != null) {
                                adDownload.didPresentFullScreen((AdViewCore)view);
                            }
                                                        
                        } else {
                            Toast.makeText(context, "Internet is not available", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else if (isAccessNetworkState == PackageManager.PERMISSION_DENIED) {
                        adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "OverrideUrlLoading - openUrlInExternalBrowser", url);
                        if(adDownload != null) {
                            adDownload.willPresentFullScreen((AdViewCore)view);
                            adDownload.willLeaveApplication((AdViewCore)view);
                        }
                        openUrlInExternalBrowser(context, url);
                    }
                }
            } catch (Exception e) {
              Log.e("TapIt", "error while calling shouldOverrideUrlLoading(" + url + ")", e);
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "shouldOverrideUrlLoading",
                        e.getMessage());
            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("TapIt", "onPageStarted: " + url);
            numPagesLoading++;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            numPagesLoading--;
            Log.d("TapIt", "onPageFinished("+ numPagesLoading +"): " + url);
            if (numPagesLoading == 0) {
              if(AdViewCore.this.mraid) {
                  if (getMraidState() == MraidState.LOADING) {
                      fireMraidEvent(MraidEvent.READY, null);
                      setMraidState(MraidState.DEFAULT);
                      syncMraidState();
                      fireMraidEvent(MraidEvent.STATECHANGE, mraidState.value);
                  }
              }
            ((AdViewCore) view).onPageFinished();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            try {
                if (adDownload != null) {
                    adDownload.error((AdViewCore)view, description);
                }
            } catch (Exception e){
                Log.e("TapIt", "An error occurred", e);
            }
        }
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.confirm();
//            return super.onJsAlert(view, url, message, result);
            return true;
        }
    };

    private void openUrlInExternalBrowser(Context context, String url) {
                
        if (openInInternalBrowser){
            try {
                AdActivity.callingAdView = this;
                Intent intent = new Intent(context, AdActivity.class);
                intent.setData(Uri.parse(url));
                intent.putExtra("com.tapit.adview.ClickURL", url);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "openUrlInExternalBrowser",
                        e.getMessage() + " - Page will open in system browser.");
                try {
                    if(adDownload != null) {
                        adDownload.willLeaveApplication(this);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } catch (Exception ex) {
                    adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "openUrlInExternalBrowser",
                            ex.getMessage());
                }
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "openUrlInExternalBrowser",
                        e.getMessage());
            }            
        } else {
            try {
                if(adDownload != null) {
                    adDownload.willLeaveApplication(this);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "openUrlInExternalBrowser",
                        e.getMessage());
            }
        }
    }
        
    /**
     * Hackish inter-activity communication... should only be called by AdActivity...
     */
    void willDismissFullScreen() {
        if(adDownload != null) {
            adDownload.willDismissFullScreen(this);
        }
    }

    private boolean isInternetAvailable(Context context) {
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if ((networkInfo != null) && (networkInfo.isAvailable())) {
            result = true;
        }

        return result;
    }

    private static int requestCounter = 0;

    private String requestGet(String url) throws IOException {
        requestCounter++;
        int rcounterLocal = requestCounter;

        adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO,
                "requestGet[" + String.valueOf(rcounterLocal) + "]", url);

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 8192);
        String responseValue = readInputStream(bufferedInputStream);
        bufferedInputStream.close();
        inputStream.close();
        adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO,
                "requestGet result[" + String.valueOf(rcounterLocal) + "]", responseValue);
        return responseValue;
    }

    private static String readInputStream(BufferedInputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] buffer = new byte[8192];
        for (int n; (n = in.read(buffer)) != -1;) {
            out.append(new String(buffer, 0, n));
        }
        return out.toString();
    }

    private static Integer getIntParameter(String stringValue) {
        if (stringValue != null) {
            return Integer.parseInt(stringValue);
        } else {
            return null;
        }
    }

    private static Long getLongParameter(String stringValue) {
        if (stringValue != null) {
            return Long.parseLong(stringValue);
        } else {
            return null;
        }
    }
        
    public void injectJavaScript(String str) {
        super.loadUrl("javascript:" + str);
    }

    public String getState() {
        return mViewState.toString().toLowerCase();
    }

    public void resize(int height, int width, boolean isModal, String twoPartCreativeUrl) {
        //TODO if isModal, then load in an AdActivity?

        FrameLayout rootView = (FrameLayout)getRootView().findViewById(android.R.id.content);

        if (placeholderView == null) {
            // pull the ad out of the view hierarchy and put it on top of the stack
            placeholderView = new FrameLayout(this.getContext());
            placeholderView.setBackgroundColor(Color.RED);
            placeholderView.setVisibility(View.INVISIBLE);
            swapViews(this, placeholderView);
        }

        rootView.bringToFront();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
//        params.gravity = Gravity.CENTER; // this causes taps to be misaligned for some reason...
        rootView.addView(this, params);

        OnAdDownload clickListener = getOnAdDownload();
        if(clickListener != null) {
            clickListener.willPresentFullScreen(this);
            clickListener.didPresentFullScreen(this);
        }
        cancelUpdating();
    }

    public void mraidClose() {
        if (placeholderView != null) {
            swapViews(placeholderView, this);
            placeholderView = null;
        }

        if (MraidState.DEFAULT.equals(getMraidState())) {
            setMraidState(MraidState.HIDDEN);
            setVisibility(View.GONE);
            fireMraidEvent(MraidEvent.VIEWABLECHANGE, "false");
        }
        else {
            setMraidState(MraidState.DEFAULT);
            OnAdDownload clickListener = getOnAdDownload();
            if(clickListener != null) {
                clickListener.willDismissFullScreen(this);
            }
        }
        fireMraidEvent(MraidEvent.STATECHANGE, mraidState.value);
        syncMraidState();

        scheduleUpdate();
    }

    /**
     * Pulls viewInLayout out of the view hierarchy, placing altView in it's place.
     * @param viewInLayout the view that's currently in the hierarchy.
     * View will be left w/o a parent.
     * @param altView the view that will replace viewInLayout's position in hierarchy.
     * This view will assume all layout params of viewInLayout
     */
    private static void swapViews(ViewGroup viewInLayout, ViewGroup altView) {
        ViewGroup parent = (ViewGroup) viewInLayout.getParent();
        if (parent == null) {
            TILog.w("Failed to swapViews because viewInLayout has no parent");
            return;
        }

        int index;
        int count = parent.getChildCount();
        for (index = 0; index < count; index++) {
            if (parent.getChildAt(index) == viewInLayout) {
                break;
            }
        }

        parent.removeView(viewInLayout);
        ViewGroup.LayoutParams lp = viewInLayout.getLayoutParams();
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
//                viewInLayout.getWidth(),
//                viewInLayout.getHeight()
//        );
        ViewGroup altParent = (ViewGroup)altView.getParent();
        if(altParent != null) {
            altParent.removeView(altView);
        }
        parent.addView(altView, index, lp);
    }

    public void resize(int width, int height) {
        throw new UnsupportedOperationException("Can't resize: " + width + ", " + height);

//        Message msg = mHandler.obtainMessage(MESSAGE_RESIZE);
//
//        Bundle data = new Bundle();
//        data.putInt(RESIZE_WIDTH, width);
//        data.putInt(RESIZE_HEIGHT, height);
//        msg.setData(data);
//
//        mHandler.sendMessage(msg);
    }

    /**
     * Revert to earlier ad state
     */
    public void resetContents() {

        FrameLayout contentView = (FrameLayout) getRootView().findViewById(
                R.id.content);

        FrameLayout placeHolder = (FrameLayout) getRootView().findViewById(
                PLACEHOLDER_ID);
        FrameLayout background = (FrameLayout) getRootView().findViewById(
                BACKGROUND_ID);
        ViewGroup parent = (ViewGroup) placeHolder.getParent();
        background.removeView(this);
        contentView.removeView(background);
        resetLayout();
        parent.addView(this, mIndex);
        parent.removeView(placeHolder);
        parent.invalidate();
    }

        
    public void close() {
        throw new UnsupportedOperationException("Can't close...");
//        mHandler.sendEmptyMessage(MESSAGE_CLOSE);
    }

    public void hide() {
        throw new UnsupportedOperationException("Can't hide...");
//        mHandler.sendEmptyMessage(MESSAGE_HIDE);
    }

    public void show() {
        throw new UnsupportedOperationException("Can't hide...");
//        mHandler.sendEmptyMessage(MESSAGE_SHOW);
    }
        
    public void expand(Dimensions dimensions, String URL, Properties properties) {
        throw new UnsupportedOperationException("Can't expand: " + dimensions + "; " + URL + "; " + properties);
//        Message msg = mHandler.obtainMessage(MESSAGE_EXPAND);
//
//        Bundle data = new Bundle();
//        data.putParcelable(DIMENSIONS, dimensions);
//        data.putString(EXPAND_URL, URL);
//        data.putParcelable(EXPAND_PROPERTIES, properties);
//        msg.setData(data);
//
//        mHandler.sendMessage(msg);
    }

    public void useCustomCloseButton(boolean useCustomClose) {
    }

    public void open(String url) {
        open(url, true, true, true);
    }
        
    /**
     * Open.
     * 
     * @param url
     *            the url
     * @param back
     *            show the back button
     * @param forward
     *            show the forward button
     * @param refresh
     *            show the refresh button
     */
    public void open(String url, boolean back, boolean forward, boolean refresh) {
        openUrlInExternalBrowser(getContext(), url);
    }
        
        /**
     * Play video
     * 
     * @param url
     *            - video URL
     * @param audioMuted
     *            - should audio be muted
     * @param autoPlay
     *            - should video play immediately
     * @param controls
     *            - should native player controls be visible
     * @param loop
     *            - should video start over again after finishing
     * @param d
     *            - inline area dimensions
     * @param startStyle
     *            - normal/fullscreen; full screen if video should play in full
     *            screen
     * @param stopStyle
     *            - normal/exit; exit if video should exit after video stops
     */
    public void playVideo(String url, String clickUrl, boolean audioMuted, boolean autoPlay,
            boolean controls, boolean loop, Dimensions d, String startStyle,
            String stopStyle) {

        throw new UnsupportedOperationException("Can't play: " + url + "; " + clickUrl);

//        Message msg = mHandler.obtainMessage(MESSAGE_PLAY_VIDEO);
//
//        PlayerProperties properties = new PlayerProperties();
//
//        properties.setProperties(audioMuted, autoPlay, controls, false,loop,
//                startStyle, stopStyle);
//
//        Bundle data = new Bundle();
//        data.putString(EXPAND_URL, url);
//        data.putString(ACTION_KEY, ACTION.PLAY_VIDEO.toString());        
//        
//        data.putParcelable(PLAYER_PROPERTIES, properties);
//        
//        if(d != null)
//            data.putParcelable(DIMENSIONS, d);
//
//        if (properties.isFullScreen()) {
//            try {
////                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "loadUrl", "ORMMA Fullscreen ad not supported");
//                Intent intent = new Intent(getContext(), OrmmaActionHandler.class);
//                intent.putExtras(data);
//                getContext().startActivity(intent);
//            }
//            catch(ActivityNotFoundException e){
//                Log.e("TapIt", "An error occured", e);
//            }
//        } else if(d != null){
//            msg.setData(data);
//            mHandler.sendMessage(msg);
//        }
    }

    protected void closeExpanded(View expandedFrame) {
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView())
                .removeView(expandedFrame);
        requestLayout();
    }

    public void loadUrl(String url, boolean dontLoad, String dataToInject) {
        mDataToInject = dataToInject;
        if (!dontLoad) {
            try {
                if ((url != null) && (url.length() > 0)) {
                    super.loadUrl(url);
                }
                return;
            } catch (Exception e) {
                Log.e("TapIt", "An error occured", e);
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "loadUrl", e.getMessage());
                return;
            }
        } else {
            if ((mContent != null) && (mContent.length() > 0)) {
//                super.setBackgroundColor(Color.WHITE);
                loadDataWithBaseURL(url, mContent);
            }
        }
    }

    protected void onPageFinished() {
        if (mDataToInject != null){
            injectJavaScript(mDataToInject);
        }
        if (animateBack){
                        
            final float centerX = getWidth() / 2.0f;
            final float centerY = getHeight() / 2.0f;

            // Create a new 3D rotation with the supplied parameter
            // The animation listener is used to trigger the next animation
            final Rotate3dAnimation rotation =
                    new Rotate3dAnimation(-90, 0, centerX, centerY, 310.0f, false);
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());

            handler.post(new Runnable() {
                                
                @Override
                public void run() {
                    startAnimation(rotation);
                    animateBack = false;
                }
            });
        }
    }


    public void setContent(String content) {
        mContent = content;
    }

    /**
     * Required. Set the id of the zone of publisher site.
     */
    public void setZone(String zone) {
        if (adRequest != null) {
            adRequest.setZone(zone);
        }
    }

    /**
     * Get the id of the zone of publisher site.
     */
    public String getZone() {
        if (adRequest != null) {
            return adRequest.getZone();
        } else {
            return null;
        }
    }

    /**
     * Optional. Set the adtype of the advertise.
     */
    public void setAdtype(String adtype) {
        if (adRequest != null) {
            adRequest.setAdtype(adtype);
        }
    }

    /**
     * Get the adtype of the advertise.
     */
    public String getAdtype() {
        if (adRequest != null) {
            return adRequest.getAdtype();
        } else {
            return null;
        }
    }

    /**
     * @deprecated
     * Optional. Set minimum width of advertising.
     */
    public void setMinSizeX(Integer minSizeX) {
        if ((adRequest != null)) {
            adRequest.setMinSizeX(minSizeX);
        }
    }

    /**
     * @deprecated
     * Optional. Get minimum width of advertising.
     */
    public Integer getMinSizeX() {
        if (adRequest != null) {
            return adRequest.getMinSizeX();
        } else {
            return null;
        }
    }

    /**
     * @deprecated
     * Optional. Set minimum height of advertising.
     */
    public void setMinSizeY(Integer minSizeY) {
        if ((adRequest != null)) {
            adRequest.setMinSizeY(minSizeY);
        }
    }

    /**
     * @deprecated
     * Optional. Get minimum height of advertising.
     */
    public Integer getMinSizeY() {
        if (adRequest != null) {
            return adRequest.getMinSizeY();
        } else {
            return null;
        }
    }

    /**
     * @deprecated
     * Optional. Set maximum width of advertising.
     */
    public void setMaxSizeX(Integer maxSizeX) {
        if ((adRequest != null)) {
            adRequest.setSizeX(maxSizeX);
        }
    }

    /**
     * @deprecated
     * Optional. Get maximum width of advertising.
     */
    public Integer getMaxSizeX() {
        if (adRequest != null) {
            return adRequest.getSizeX();
        } else {
            return null;
        }
    }

    /**
     * @deprecated
     * Optional. Set maximum height of advertising.
     */
    public void setMaxSizeY(Integer maxSizeY) {
        if ((adRequest != null)) {
            adRequest.setSizeY(maxSizeY);
        }
    }

    /**
     * @deprecated
     * Optional. Get maximum height of advertising.
     */
    public Integer getMaxSizeY() {
        if (adRequest != null) {
            return adRequest.getSizeY();
        } else {
            return null;
        }
    }

    /**
     * Optional. Set Background color of advertising in HEX.
     */
    public void setBackgroundColor(String backgroundColor) {
        try {
            int iColor = Integer.decode("#" + backgroundColor);
            setBackgroundColor(iColor);
        } catch(NumberFormatException e) {
            adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "AdViewCore.setBackgroundColor", e.getMessage());
        }
                
        if (adRequest != null) {
            try {
                adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "setBackgroundColor", "#"
                        + backgroundColor);
                adRequest.setParamBG(backgroundColor);
            } catch (Exception e) {
                adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "AdViewCore.setBackgroundColor",
                        e.getMessage());
            }
        }
    }
        
    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        super.setBackgroundColor(color);
    }

    /**
     * Optional. Get Background color of advertising in HEX.
     */
    public String getBackgroundColor() {
        if (adRequest != null) {
            return adRequest.getParamBG();
        } else {
            return "FFFFFF";
        }
    }

    /**
     * Optional. Set Text color of links in HEX.
     */
    public void setTextColor(String textColor) {
        if (adRequest != null) {
            adRequest.setParamLINK(textColor);
        }
    }

    /**
     * Optional. Get Text color of links in HEX.
     */
    public String getTextColor() {
        if (adRequest != null) {
            return adRequest.getParamLINK();
        } else {
            return null;
        }
    }

    /**
     * Optional. Overrides the URL of ad server.
     */
    public void setAdserverURL(String adserverURL) {
        if (adRequest != null) {
            adRequest.setAdserverURL(adserverURL);
        }
    }

    /**
     * Optional. Get URL of ad server.
     */
    public String getAdserverURL() {
        if (adRequest != null) {
            return adRequest.getAdserverURL();
        } else {
            return null;
        }
    }

    /**
     * Optional. Set user location latitude value (given in degrees.decimal
     * degrees).
     */
    public void setLatitude(String latitude) {
        if ((adRequest != null) && (latitude != null)) {
            adRequest.setLatitude(latitude);
        }
    }

    /**
     * Optional. Get user location latitude value (given in degrees.decimal
     * degrees).
     */
    public String getLatitude() {
        if (adRequest != null) {
            String latitude = adRequest.getLatitude();

            if (latitude != null) {
                return latitude;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Optional. Set user location longtitude value (given in degrees.decimal
     * degrees).
     */
    public void setLongitude(String longitude) {
        if ((adRequest != null) && (longitude != null)) {
            adRequest.setLongitude(longitude);
        }
    }

    /**
     * Optional. Get user location longtitude value (given in degrees.decimal
     * degrees).
     */
    public String getLongitude() {
        if (adRequest != null) {
            String longitude = adRequest.getLongitude();

            if (longitude != null) {
                return longitude;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getClickURL() {
        return mClickURL;
    }
        
    /**
     * 
     * AdLog.LOG_LEVEL_NONE    none<br>
     * AdLog.LOG_LEVEL_1     only errors<br>
     * AdLog.LOG_LEVEL_2     +warning<br>
     * AdLog.LOG_LEVEL_3     +server traffic<br>
     */
    public void setLogLevel(int logLevel) {
        adLog.setLogLevel(logLevel);
    }

    /**
     * Whether to open pages in internal browser or in system browser.
     * Defaul values is true.
     */
    public void setOpenInInternalBrowser(boolean b){
        openInInternalBrowser = b;
    }

    public void raiseError(String strMsg, String action){
        throw new UnsupportedOperationException("Can't raise error: " + strMsg + "; " + action);
//        Message msg = mHandler.obtainMessage(MESSAGE_RAISE_ERROR);
//
//        Bundle data = new Bundle();
//        data.putString(ERROR_MESSAGE, strMsg);
//        data.putString(ERROR_ACTION, action);
//        msg.setData(data);
//        mHandler.sendMessage(msg);
    }
        
    /**
     * Reset layout.
     */
    private void resetLayout() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if(bGotLayoutParams) {
            lp.height = mInitLayoutHeight;
            lp.width = mInitLayoutWidth;
        }
        setVisibility(VISIBLE);
        requestLayout();
    }
        
    /**
     * Set enabled or disabled animation between banners
     * @param b true - enable animation, false - disable
     */
    public void setBannerAnimationEnabled(boolean b){
        isBannerAnimationEnabled = b;
    }
        
    /**
     * return whether banner animation enabled
     * @return true if animated
     */
    public boolean isBannerAnimationEnabled(){
        return isBannerAnimationEnabled;
    }

    public int getAdHeight() {
        return adHeight;
    }

    public int getAdWidth() {
        return adWidth;
    }

    /**
     * Convenience method for loading html as a string, and setting the background color
     *  
     * @param data the string to be loaded as html
     */
    protected void loadDataWithBaseURL(String url, String data) {
        // http://stackoverflow.com/questions/2704929/uncaught-error-security-err-dom-exception-18
        super.loadDataWithBaseURL(url, data, "text/html", "UTF-8", "about:blank");
        // reset background
        if(backgroundColor != null) {
            setBackgroundColor(backgroundColor);
        }
    }

    // Stub to quell errors relating to stripping out ORMMA
    static class Dimensions {

    }

    // Stub to quell errors relating to stripping out ORMMA
    static class Properties {

    }

    /////////////////
    // MRAID Code //
    ////////////////


  public MraidState getMraidState() {
    return mraidState;
  }

  public void setMraidState(MraidState mraidState) {
    this.mraidState = mraidState;
  }

  /**
   * Test if html an mraid ad
   * @param html the html string to test
   * @return true if mraid, false otherwise
   */
    protected boolean checkIfMraid(String html) {
      return html.toLowerCase().contains("mraid.js");
    }

  /**
   * router code to parse and direct native calls to the appropriate
   * mraid command handler
   * @param urlStr the request in REST format
   */
    protected void handleNativeMraidCall(String urlStr) {
      Log.d("TapIt", "handleNativeMraidCall(" + urlStr + ")");
      MraidCommand.routeRequest(urlStr, this);
    }

    private int commandCounter = 0; //TODO remove debugging code
  /**
   * Mechanism to send data back to the js mraid bridge.
   * @param data the data to send
   * @param callbackToken the token used to mark data as a response to a js request,
   *                      or null for unsolicited data (e.g. one way comms)
   */
    protected void mraidResponse(Map<String, ?> data, String callbackToken) {
      StringBuilder sb = new StringBuilder("{");
      if (data != null) {
//        Log.d("TapIt", "DATA: " + data);
        boolean isFirst = true;
        for (Map.Entry<String, ?> entry : data.entrySet()) {
          if(isFirst) {
            isFirst = false;
          }
          else {
            sb.append(",");
          }
          sb.append(entry.getKey());
          sb.append(":");

          Object v = entry.getValue();
          if(v instanceof String) {
            String vStr = (String)v;
            if(!vStr.startsWith("{") && !vStr.startsWith("[")) {
              vStr = String.format("\"%s\"", v);
            }
            sb.append(vStr);
          }
          else if (v == null) {
            sb.append("null");
          }
          else {
            sb.append(v);
          }
        }
      }
      sb.append("}");
      String dataStr = sb.toString();

      commandCounter++;
      String jsStr;
      if (callbackToken != null) {
          //TODO remove debugging code!
        jsStr = String.format("mraid._nativeResponse(%s, \"%s\");console.debug(%d + ': command success!');", dataStr, callbackToken, commandCounter);
      }
      else {
          //TODO remove debugging code!
        jsStr = String.format("mraid._nativeResponse(%s);console.debug(%d + ': command success!');", dataStr, commandCounter);
      }

      Log.d("TapIt", "mraidResponse: " + jsStr);
      injectJavaScript(jsStr);
    }

    protected void fireMraidEvent(MraidEvent mraidEvent, String dataString) {
      if(dataString != null && !dataString.startsWith("[")) {
        dataString = String.format("[\"%s\"]", dataString);
      }

      String eventStr;
      if(dataString != null) {
        eventStr = String.format("{name:\"%s\", props:%s}", mraidEvent.value, dataString);
      }
      else {
        eventStr = String.format("{name:\"%s\"}", mraidEvent.value);
      }

      Map<String, String> data = new HashMap<String, String>();
      data.put("_fire_event_", eventStr);
      mraidResponse(data, null);
    }

    protected void syncMraidState() {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();


        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("state", mraidState.value);
        params.put("isVisible", getVisibility() == View.VISIBLE);
        params.put("height", height);
        params.put("width", width);
        params.put("x", 0);
        params.put("y", 0);
        params.put("placementType", getMraidPlacementType().toString());

        mraidResponse(params, null);
    }
}