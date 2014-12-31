package com.tapit.advertising.internal;

import android.content.Context;
import com.tapit.advertising.TapItAdLoader;
import com.tapit.advertising.TapItAdRequest;
import com.tapit.advertising.TapItNativeAd;
import com.tapit.core.TapItLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NativeAdLoader implements TapItAdLoader<TapItNativeAd> {
    private static final String TAG = "TapIt";

    @Override
    public void multiLoad(final Context context, final TapItAdRequest request,
                          final int maxAdsRequested, final TapItAdLoaderListener<TapItNativeAd> loaderCallback) {
        //TODO code smell?  maybe ad manager shouldn't return an AdResponse, or AdResponse should support multiple responses
        final AdManager adManager = new AdManager(new AdManager.ResultsCallback() {
            @Override
            public void onSuccess(AdResponseBuilder.AdResponse response) {
                // noop
            }

            @Override
            public void onError(String errorMsg) {
                // noop
            }
        }) {
            @Override
            public void asyncResponse(String responseStr) {
                TapItLog.d(TAG, "Response: " + responseStr);

                if (responseStr == null || "".equals(responseStr.trim())) {
                    // error w/o a message, use generic error
                    loaderCallback.onFail(NativeAdLoader.this, "Server returned an empty response.");
                    return;
                }

                try {
                    JSONArray jsonArray = new JSONArray(responseStr);
                    if (jsonArray.length() == 1) {
                        JSONObject firstJson = jsonArray.getJSONObject(0);
                        if (firstJson.has("error")) {
                            loaderCallback.onFail(NativeAdLoader.this, firstJson.getString("error"));
                            return;
                        }
                    }
                    else if(jsonArray.length() == 0) {
                        loaderCallback.onFail(NativeAdLoader.this, "Server returned no ads.");
                        return;
                    }

                    final List<TapItNativeAd> adList = new ArrayList<TapItNativeAd>(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        TapItNativeAd ad = NativeAdImpl.getNativeAd(context, request);
                        ((NativeAdImpl)ad).loadFromJson(jsonArray.getString(i));
                        if(ad.isLoaded()) {
                            adList.add(ad);
                        }
                    }
                    if (adList.size() > 0) {
                        loaderCallback.onSuccess(NativeAdLoader.this, adList);
                    }
                    else {
                        loaderCallback.onFail(NativeAdLoader.this, "No ads were returned");
                    }
                } catch (JSONException e) {
                    loaderCallback.onFail(NativeAdLoader.this, "An error occured while processing response");
                    TapItLog.e(TAG, "An error occured while processing response", e);
                }
            }
        };

        final AdRequestUrlBuilder requestUrlBuilder = new AdRequestUrlBuilder(request, context);
        requestUrlBuilder.setMaxAdsRequested(maxAdsRequested);
        adManager.submitRequest(context, requestUrlBuilder);
    }
}
