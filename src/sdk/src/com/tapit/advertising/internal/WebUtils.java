package com.tapit.advertising.internal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.webkit.WebView;
import com.tapit.core.TapItLog;
import org.apache.http.client.ClientProtocolException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WebUtils {
    private static final Object LOCK = new Object();

    private static final String TAG = "TapIt";

    private WebUtils() {} // non-instantiable util class

    public static String wrapHtml(String html) {
        return wrapHtml(html, -1, -1);
    }

    public static String wrapHtml(final String html, final int width, final int height) {
        if (html.toLowerCase().contains("<html")) {
            // fully formed html, don't modify
            return html;
        }
        String header;
        if (width > 0 && height > 0) {
            header = String.format("<html><body style=\"margin:0;padding:0;width:%d;height:%d;\">", width, height);
        }
        else {
            header = "<html><body style=\"margin:0;padding:0;\">";
        }
        String footer = "</body></html>";

        return header + html + footer;
    }

    public static boolean isGooglePlayUrl(String url) {
        return url.startsWith("market://details?") ||
                url.startsWith("http://market.android.com/details?") ||
                url.startsWith("https://market.android.com/details?") ||
                url.startsWith("http://play.google.com/store/apps/details?") ||
                url.startsWith("https://play.google.com/store/apps/details?");
    }

    private static String userAgent = null;
    public static String getDefaultUA(Context context) {
        if (userAgent == null) {
            synchronized (LOCK) {

            }
        }
        WebView webView = new WebView(context);
        return webView.getSettings().getUserAgentString();
    }

    /**
     * Defers building the url until in the background, which allows you to do things
     * such as pull the google advertising id
     */
    public interface UrlBuilder {
        public String buildUrl();
    }

    public interface AsyncHttpResponseListener {
        public void asyncResponse(String response);
        public void asyncError(Throwable throwable);
    }

    public static void asyncHttpRequest(final Context context,
                                        final String url,
                                        final AsyncHttpResponseListener callback) {

        final String ua = getDefaultUA(context);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }

        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urls) {
                String urlStr = urls[0]; // don't send more than one url at a time!
                TapItLog.d(TAG, "Request: " + urlStr);
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(urlStr);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("User-Agent", ua);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    StringBuilder sb = new StringBuilder(8192);
                    byte[] buffer = new byte[8192];
                    for(int i; (i = in.read(buffer)) != -1;) {
                        sb.append(new String(buffer, 0, i, "UTF-8"));
                    }
                    return sb.toString();
                } catch (ClientProtocolException e) {
                    callback.asyncError(e);
                } catch (IOException e) {
                    callback.asyncError(e);
                }
                finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.asyncResponse(result);
                }
            }
        };

        if(Build.VERSION.SDK_INT >= 11) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
        else {
            asyncTask.execute(url);
        }
    }

    public static String urlEncode(String str) {
        if (str ==  null) {
            throw new IllegalArgumentException("str must not be null!");
        }

        String encoded = null;
        try {
            encoded = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not be possible...
            TapItLog.e(TAG, "Bad encoding", e);
        }
        assert encoded != null : "Got a null encoded string!";
        return encoded;
    }
}
