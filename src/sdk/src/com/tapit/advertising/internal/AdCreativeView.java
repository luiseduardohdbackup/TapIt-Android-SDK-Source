package com.tapit.advertising.internal;

import android.content.Context;
import android.util.AttributeSet;

public class AdCreativeView extends BasicWebView {
    public AdCreativeView(Context context) {
        super(context);
    }

    public AdCreativeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdCreativeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //TODO capability enbabler - e.g. look for indicatiors that this is an MRAID ad, then enable the MRAID capability

    public void loadPwAdResponse(AdResponseBuilder.AdResponse response) {
        //TODO add a done/error callback?
        String html = response.getAdMarkup();
        loadData(html, "text/html", "UTF-8");
    }

}
