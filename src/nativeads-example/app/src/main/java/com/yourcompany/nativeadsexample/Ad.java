package com.yourcompany.nativeadsexample;

import android.content.Context;

import com.tapit.advertising.TapItNativeAd;

import org.json.JSONException;
import org.json.JSONObject;

public class Ad {
    private final String adtitle;
    private final String imageurl;
    private final int stars;
    private final String html;
    private final String adtext;
    private final String cta;
    private final TapItNativeAd tapItNativeAd;

    public Ad(TapItNativeAd tapItNativeAd) throws JSONException {
        this.tapItNativeAd = tapItNativeAd;
        JSONObject json = new JSONObject(tapItNativeAd.getAdData());
        this.adtitle = json.optString("adtitle");
        this.imageurl = json.optString("iconurl");
        this.stars = json.optInt("rating");
        this.html = json.optString("html");
        this.adtext = json.optString("adtext");
        this.cta = json.optString("cta");
    }

    public String getAdtitle() {
        return adtitle;
    }

    public String getImageurl() {
        return imageurl;
    }

    public int getStars() {
        return stars;
    }

    public String getHtml() {
        return html;
    }

    public String getAdtext() {
        return adtext;
    }

    public String getCta() {
        return cta;
    }

    public void impression() {
        tapItNativeAd.trackImpression();
    }

    public void click(Context ctx) {
        tapItNativeAd.click(ctx);
    }

    @Override
    public String toString() {
        return "Title: " + adtitle + " (" + tapItNativeAd.toString() + ")";
    }
}
