package com.tapit.advertising.internal;

import com.tapit.core.TapItLog;
import org.json.JSONException;
import org.json.JSONObject;

public class AdResponseBuilder {

    public static class NoAdReturnedException extends Exception {
        NoAdReturnedException(String message) {
            super(message);
        }

        NoAdReturnedException(String message, Throwable causedBy) {
            super(message, causedBy);
        }
    }



    public static interface AdResponse {
        public String getRawData();
        public String getType();
        public int getWidth();
        public int getHeight();
        public boolean isMraid();
        public String getHtml();

        public String getAdMarkup();

    }


    public AdResponse buildFromString(final String strResponse) throws NoAdReturnedException {
        AdResponse adResponse = null;

        if (strResponse == null || "".equals(strResponse.trim())) {
            // error w/o a message, use generic error
            throw new NoAdReturnedException("Server returned an empty response.");
        }

        try {
            JSONObject jsonObject = new JSONObject(strResponse);
            if(jsonObject.has("error")) {
                // failed to retrieve an ad, abort and call the error callback
                throw new NoAdReturnedException(jsonObject.getString("error"));
            }
            else {
                final String type = (jsonObject.has("type") ? jsonObject.getString("type") : null);
                final boolean isMraid = (jsonObject.has("mraid") && jsonObject.getBoolean("mraid"));
                final int width = (jsonObject.has("adWidth") ? Integer.parseInt(jsonObject.getString("adWidth")) : 0);
                final int height = (jsonObject.has("adHeight") ? Integer.parseInt(jsonObject.getString("adHeight")) : 0);
                final String html = jsonObject.getString("html");

                adResponse = new AdResponse() {
                    @Override
                    public String getRawData() {
                        return strResponse;
                    }

                    @Override
                    public String getType() {
                        return type;
                    }

                    @Override
                    public int getWidth() {
                        return width;
                    }

                    @Override
                    public int getHeight() {
                        return height;
                    }

                    @Override
                    public boolean isMraid() {
                        return isMraid;
                    }

                    @Override
                    public String getHtml() {
                        return html;
                    }

                    @Override
                    public String getAdMarkup() {
                        return WebUtils.wrapHtml(getHtml(), getWidth(), getHeight());
                    }
                };
            }
        } catch (JSONException e) {
            // response is not JSON, assume response is error description
            TapItLog.e("TapIt", "failed to parse JSON", e);
            throw new NoAdReturnedException(strResponse, e);
        }

        return adResponse;
    }

}
