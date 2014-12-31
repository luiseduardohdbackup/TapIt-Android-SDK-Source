package com.tapit.advertising.internal;

import android.content.Context;
import android.view.View;
import com.tapit.advertising.TapItAdRequest;
import com.tapit.advertising.TapItNativeAd;
import com.tapit.core.TapItLog;
import org.json.JSONException;
import org.json.JSONObject;

public class NativeAdImpl implements TapItNativeAd {
    private static enum State { NEW, LOADING, DONE }

    private static final String TAG = "TapIt";

    private final TapItAdRequest adRequest;
    private final Object lock = new Object();

    private State state = State.NEW;
    private TapItNativeAdListener listener = null;
    private Context context = null;
    private AdResponseBuilder.AdResponse adResponse = null;
    private boolean impressionTracked = false;

    /**
     * Factory method which generates a Native Ad object
     * @param context Application context that will be used to build the native ad
     * @param zone Identifier of ad placement to be loaded.
     * @return A native ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static TapItNativeAd getNativeAdForZone(Context context, String zone) {
        TapItAdRequest request = new AdRequestImpl.BuilderImpl(zone).getTapItAdRequest();
        return NativeAdImpl.getNativeAd(context, request);

    }

    /**
     * Factory method which generates a native ad object
     * @param context Application context that will be used to build the native ad
     * @param request Request object used to hold request configuration details.
     * @return A native ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static TapItNativeAd getNativeAd(Context context, TapItAdRequest request) {
        return new NativeAdImpl(context, request);

    }

    private NativeAdImpl(Context context, TapItAdRequest request) {
        this.context = context;
        this.adRequest = request;
    }

    @Override
    public void setListener(TapItNativeAdListener listener) {
        this.listener = listener;
    }

    @Override
    public void load() {
        if (state != State.NEW) {
            TapItLog.w(TAG, "Ignoring attempt to re-use ad.");
            return;
        }

//        final AdManager adManager = buildMockAdManager();
        final AdManager adManager = buildAdManager();
        final AdRequestUrlBuilder requestUrlBuilder = new AdRequestUrlBuilder(adRequest, context);
//        requestUrlBuilder.setLocation(latitude, longitude);
        state = State.LOADING;
        adManager.submitRequest(context, requestUrlBuilder);
    }

    /**
     * Should not be used directly!
     * Used by NativeAdLoader when loading multiple ads at once
     * @param jsonData json string used to build the native ad
     */
    void loadFromJson(String jsonData) {
        TapItLog.d(TAG, "loadFromJson: " + jsonData);
        state = State.DONE;
        try {
            AdResponseBuilder responseBuilder = new AdResponseBuilder();
            adResponse = responseBuilder.buildFromString(jsonData);
            if (listener != null) {
                listener.nativeAdDidLoad(NativeAdImpl.this);
            }

        } catch (AdResponseBuilder.NoAdReturnedException e) {
            TapItLog.e(TAG, "failed to parse ad", e);
            if(listener != null) {
                listener.nativeAdDidFail(NativeAdImpl.this, "failed to parse ad");
            }
        }

    }

    private AdManager buildAdManager() {
        AdManager.ResultsCallback callback = new AdManager.ResultsCallback() {
            @Override
            public void onSuccess(AdResponseBuilder.AdResponse response) {
                adResponse = response;
                state = State.DONE;
                if (listener != null) {
                    listener.nativeAdDidLoad(NativeAdImpl.this);
                }
            }

            @Override
            public void onError(String errorMsg) {
                state = State.DONE;
                if(listener != null) {
                    listener.nativeAdDidFail(NativeAdImpl.this, errorMsg);
                }
            }
        };

        return new AdManager(callback);
    }

    @Override
    public boolean isLoaded() {
        return state == State.DONE && adResponse != null;
    }

    @Override
    public String getAdData() {
        if (!isLoaded()) {
            throw new IllegalStateException("Data isn't loaded yet!");
        }

        return adResponse.getRawData();
    }

    @Override
    public void trackImpression() {
        if (!isLoaded()) {
            throw new IllegalStateException("Data isn't loaded yet!");
        }

        if (!impressionTracked) {
            synchronized (lock) {
                if (!impressionTracked) {
                    impressionTracked = true;
                    try {
                        JSONObject json = new JSONObject(getAdData());
                        String pixelUrl = json.optString("impressionurl");
                        if (pixelUrl != null) {
                            WebUtils.asyncHttpRequest(context, pixelUrl, new WebUtils.AsyncHttpResponseListener() {
                                @Override
                                public void asyncResponse(String response) {
                                    // noop - we're not expecting any content in the response...
                                }

                                @Override
                                public void asyncError(Throwable throwable) {
                                    TapItLog.e(TAG, "failed to track impression", throwable);
                                }
                            });
                        }
                        else {
                            // noop - impression tracking is handled as part of the native ad content
                        }
                    } catch (JSONException e) {
                        TapItLog.e(TAG, "failed to track impression", e);
                    }
                }
            }
        }
    }

    @Override
    public void click(Context context) {
        if (!isLoaded()) {
            throw new IllegalStateException("Data isn't loaded yet!");
        }

        try {
            JSONObject json = new JSONObject(getAdData());
            String clickUrl = json.getString("clickurl");
            TapItAdActivity.startActivity(context, buildWrapper(clickUrl));
        } catch (JSONException e) {
            TapItLog.e(TAG, "failed to load click action", e);
        }
    }

    private AdActivityContentWrapper buildWrapper(final String clickUrl) {
        return new AdActivityContentWrapper() {
            private BasicWebView webView = null;

            @Override
            public View getContentView(TapItAdActivity activity) {
                if (webView == null) {
                    webView = new BasicWebView(activity);
                    webView.loadUrl(clickUrl);
                }

                return webView;
            }

            @Override
            public void done() {
            }
        };
    }

    @Override
    public String toString() {
        if (isLoaded()) {
            return getAdData();
        }
        else {
            return super.toString();
        }
    }
}
