package com.tapit.advertising.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.tapit.core.TapItLog;

/**
 * Sets up a WebView w/ the appropriate config.  Also handles forwarding external
 * urls to the system where they can be routed appropriately.
 */
public class BasicWebView extends WebView {
    private static final String TAG = "TapIt";

    /**
     * override this object to customize webview behavior
     */
    public static class BasicWebViewListener {
        /**
         * callback that allows parent to be notified when leaving application
         * (i.e. when control is transferred over to google play)
         * @param view the view causing a new app to be loaded
         */
        public void willLeaveApplication(BasicWebView view) {}

        /**
         * customize this method if you want to override the default behavior
         * of loading non http/https urls within the webview
         * @param view the web view attempting to load the url
         * @param url the url to be loaded
         * @return true if the url was handled and should *NOT* be loaded by the
         *         webview.  false if you want the webview to continue loading
         *         this url.
         */
        public boolean shouldOverrideUrlLoading(BasicWebView view, String url) {
            TapItLog.d(TAG, "BasicWebView.shouldOverrideUrlLoading(" + url + ')');
            //TODO notify any capability listeners, allow them to handle url request... e.g. MraidActivator should listen for mraid.js

            if (BasicWebView.isExternalUrl(url)) {
                willLeaveApplication(view);
                BasicWebView.openInExternalBrowser(view.getContext(), url);
                return true;
            }
            return false;
        }

        /**
         * forwards progress updates to activity (if the view's context happens
         * to be an activity)
         * @param view
         * @param newProgress
         */
        public void onProgressChanged(BasicWebView view, int newProgress) {
            if (view.getContext() instanceof Activity) {
                ((Activity)view.getContext()).setProgress(newProgress * 100);
            }
        }
    }

    private BasicWebViewListener listener = null;


    public BasicWebView(Context context) {
        super(context);
        setup();
    }

    public BasicWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public BasicWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        getSettings().setJavaScriptEnabled(true);
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        getSettings().setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (listener != null) {
                    return listener.shouldOverrideUrlLoading(BasicWebView.this, url);
                }
                return false;
            }

            //TODO offer some way to tell when page is done loading, even in the face of redirects...
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                TapItLog.d(TAG, "onPageFinished: " + url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (listener != null) {
                    listener.onProgressChanged(BasicWebView.this, newProgress);
                }
            }
        });

        setListener(new BasicWebViewListener()); // setup default listener
    }


    public BasicWebViewListener getListener() {
        return listener;
    }

    public void setListener(BasicWebViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void loadUrl(String url) {
        if (isExternalUrl(url)) {
            openInExternalBrowser(getContext(), url);
        }
        else {
            super.loadUrl(url);
        }
    }

    public void loadPwAdResponse(AdResponseBuilder.AdResponse response) {
        //TODO figure out if we need AdCreativeView or if keeping the code here...
        //TODO add a done/error callback?
        String html = response.getAdMarkup();
        loadData(html, "text/html", "UTF-8");
    }

    public static boolean isExternalUrl(String url) {
        return !(url.toLowerCase().startsWith("http://")
                  || url.toLowerCase().startsWith("https://"));
    }

    public static void openInExternalBrowser(Context context, String url) {
        TapItLog.d(TAG, "BasicWebView.openInExternalBrowser(" + url + ')');
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (context instanceof TapItAdActivity) {
            ((Activity)context).startActivityForResult(intent,3);
        }
        else {
            context.startActivity(intent);
        }
    }
}
