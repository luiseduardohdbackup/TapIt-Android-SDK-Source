package com.tapit.ads;

import android.content.Context;
import com.tapit.adview.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TapItAdRequest {


    public enum AdTypes {
        BANNER(1),
        INTERSTITIAL(2),
        OFFERWALL(7),
        ADPROMPT(9);

        public final int value;
        AdTypes(int val) {
            value = val;
        }
    }

    protected String baseUrl = DEFAULT_BASE_URL;
    protected final String zone;
    protected AdTypes adType = AdTypes.BANNER;
    protected String userAgent = null;
    protected int height = -1;
    protected int width = -1;
    protected Double latitude = null;
    protected Double longitude = null;
    protected boolean isTestMode = false;
    protected final Map<String, String> customParameters = new HashMap<String, String>(15);


    static final String DEFAULT_BASE_URL = "http://r.tapit.com/adrequest.php";

    static final String PARAMETER_ZONE = "zone";
    static final String PARAMETER_ADTYPE = "adtype";
    static final String PARAMETER_USER_AGENT = "ua";
    static final String PARAMETER_LATITUDE = "lat";
    static final String PARAMETER_LONGITUDE = "long";
    static final String PARAMETER_HEIGHT = "h";
    static final String PARAMETER_WIDTH = "w";
    static final String PARAMETER_CONNECTION_SPEED = "connection_speed";
    static final String PARAMETER_LANGUAGES = "languages";
    static final String PARAMETER_CARRIER_NAME = "carrier";
    static final String PARAMETER_CARRIER_ID = "carrier_id";
    static final String PARAMETER_DEVICE_ID = "udid";
    static final String PARAMETER_MODE = "mode";
//    static final String PARAMETER_BACKGROUND = "paramBG";
//    static final String PARAMETER_LINK = "paramLINK";
//    static final String PARAMETER_MIN_SIZE_X = "min_size_x";
//    static final String PARAMETER_MIN_SIZE_Y = "min_size_y";
//    static final String PARAMETER_SIZE_X = "size_x";
//    static final String PARAMETER_SIZE_Y = "size_y";





    public TapItAdRequest(String zone) {
        this.zone = zone;
    }

    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setTestMode(boolean testMode) {
        this.isTestMode = testMode;
    }

    /**
     * Don't set this, it'll be reset on every ad request
     * @param type the type of ad to request
     */
    public void setAdtype(AdTypes type) {
        adType = type;
    }

    private Map<String, String> getParameters(Context context) {
        Map<String, String> params = new HashMap<String, String>(customParameters.size() + 15);
        params.putAll(customParameters);
        if (isTestMode) {
            params.put(PARAMETER_MODE, "test");
        }

        if(latitude != null) {
            params.put(PARAMETER_LATITUDE, String.valueOf(latitude));
            params.put(PARAMETER_LONGITUDE, String.valueOf(longitude));
        }

        String deviceIdMD5 = Utils.getDeviceIdMD5(context);
        String carrierName = Utils.getCarrierName(context);
        String carrierId = Utils.getCarrierId(context);
        String ua = Utils.getUserAgentString(context);

        params.put(PARAMETER_DEVICE_ID, deviceIdMD5);
        params.put("format", "json");
        params.put("sdk", "android-v" + AdViewCore.VERSION);
        params.put(PARAMETER_CARRIER_NAME, carrierName);
        params.put(PARAMETER_CARRIER_ID, carrierId);
        params.put(PARAMETER_LANGUAGES, Locale.getDefault().getLanguage());
        params.put(PARAMETER_USER_AGENT, ua);

        params.put(PARAMETER_ZONE, zone);
        params.put(PARAMETER_ADTYPE, String.valueOf(adType.value));

        return params;
    }

    public void setCustomParameters(Map<String, String> cParams) {
        customParameters.clear();
        if (cParams != null) {
            customParameters.putAll(cParams);
        }
    }

    public Map<String, String> getCustomParameters() {
        return new HashMap<String, String>(customParameters);
    }

    /**
     * Used only for internal testing!
     * @param baseUrl the new base url, including scheme and endpoint...
     * e.g. http://r.tapit.com/adrequest.php
     */
    public void setBaseUrl(String baseUrl) {
        if (BuildConfig.DEBUG) {
            this.baseUrl = baseUrl;
        }
        else {
            TILog.w("Calls to setBaseUrl are ignored for release builds.");
        }
    }

    public String toUrlString(Context ctx) {
        Map<String, String> params = getParameters(ctx);
        final StringBuilder sb = new StringBuilder(256);
        sb.append(baseUrl);
        sb.append("?");
        sb.append(Utils.appendUrlParams(params));
        return sb.toString();
    }
}
