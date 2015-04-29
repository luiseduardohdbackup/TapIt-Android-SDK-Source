package com.tapit.advertising.internal;

import android.content.Context;
import com.tapit.advertising.Config;
import com.tapit.advertising.TapItAdRequest;
import com.tapit.adview.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdRequestUrlBuilder implements WebUtils.UrlBuilder {


    public static enum Orientation {
        NONE('x'), PORTRAIT('p'), LANDSCAPE('l'), ANY('a');

        public final char val;

        Orientation(char val) {
            this.val = val;
        }
    }


    private final Context context;
    private final TapItAdRequest request;
    private Double latitude = null;
    private Double longitude = null;
    private int width = 0;
    private int height = 0;
    private Orientation orientation = Orientation.NONE;
    private DeviceCapabilities.AdvertisingInfo advertisingInfo = null;
    private int maxAdsRequested = 1;
    private String requestType = null;

    public AdRequestUrlBuilder(TapItAdRequest request, Context context, RequestType requestType) {
        this.request = request;
        this.context = context;
        this.requestType = requestType.getRequestType();
    }

    public AdRequestUrlBuilder setLocation(Double lat, Double lon) {
        this.latitude = lat;
        this.longitude = lon;

        return this;
    }

    public AdRequestUrlBuilder setContainerDetails(int width, int height, Orientation orientation) {
        this.width = width;
        this.height = height;
        this.orientation = orientation;

        return this;
    }

    public AdRequestUrlBuilder setAdvertisingInfo(DeviceCapabilities.AdvertisingInfo advertisingInfo) {
        this.advertisingInfo = advertisingInfo;
        return this;
    }

    public void setMaxAdsRequested(int maxAdsRequested) {
        this.maxAdsRequested = maxAdsRequested;
    }

    public String buildUrl() {
        String url = Config.REQUEST_BASE_URL + "?format=json&zone=" + request.getZone();
        url += "&sdk=android-v" + Config.SDK_VERSION;

        if (request.isTestMode()) {
            url += "&mode=test";
        }

        String deviceidFragment = getDeviceIdUrlFragment(context); // udid, adid, ate
        if (deviceidFragment != null) {
            url += deviceidFragment;
        }

        String keywordFragment = getKeywordFragment(request); // keywords
        if (keywordFragment != null) {
            url += keywordFragment;
        }

        String LocationFragment = getLocationFragment(); // latitude, long
        if (LocationFragment != null) {
            url += LocationFragment;
        }

        String carrierFragment = getCarrierFragment(context); // carrier, carrier_id
        if (carrierFragment != null) {
            url += carrierFragment;
        }

        String containerFragment = getContainerFragment(); // w, h, o
        if (containerFragment != null) {
            url += containerFragment;
        }

        String languages = getLanguageFragment(); // languages
        if (languages != null) {
            url += languages;
        }

        String customParams = getCustomParamsFragment();
        if (customParams != null) {
            url += customParams;
        }

        if (maxAdsRequested > 1) {
            url += "&mres=" + maxAdsRequested;
        }
        url += "&rtype=" + requestType;
        return url;
    }

    private String getDeviceIdUrlFragment(Context context) {
        String fragment = null;
        if (advertisingInfo != null) {
            String advertisingId = advertisingInfo.getId();
            boolean isLAT = advertisingInfo.isLimitAdTrackingEnabled();
            if (advertisingId != null) {
                fragment = "&adid=" + WebUtils.urlEncode(advertisingId);
                if (isLAT) {
                    fragment += "&ate=0";
                }
            }
        }
        else {
            String deviceIdMD5 = Utils.getDeviceIdMD5(context);
            if ((deviceIdMD5 != null) && (deviceIdMD5.length() > 0) && !deviceIdMD5.equals("unknown")) {
                fragment = "&udid=" + WebUtils.urlEncode(deviceIdMD5);
            }
        }

        return fragment;
    }

    private String getKeywordFragment(TapItAdRequest request) {
        String frament = null;
        List<String> keywords = request.getKeywords();
        if (keywords.size() > 0) {
            StringBuilder sb = new StringBuilder(keywords.size() * 5);
            boolean first = true;
            for(String kw : keywords) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(',');
                }
                sb.append(kw);
            }

            frament = "&keywords=" + WebUtils.urlEncode(sb.toString());
        }

        return frament;
    }

    private String getLocationFragment() {
        String fragment = null;
        if (latitude != null && longitude != null) {
            fragment = "&lat=" + latitude + "&long=" + longitude;
        }
        return fragment;
    }

    private String getCarrierFragment(Context context) {
        String frament = "";
        String carrierName = Utils.getCarrierName(context);
        if (carrierName != null && !"".equals(carrierName)) {
            frament = "&carrier=" + WebUtils.urlEncode(carrierName);
        }

        String carrierId = Utils.getCarrierId(context);
        if (carrierId != null && !"".equals(carrierId)) {
            frament += "&carrier_id=" + WebUtils.urlEncode(carrierId);
        }

        return !"".equals(frament) ? frament : null;
    }

    private String getContainerFragment() {
        String fragment = "";
        if (width > 0 && height > 0) {
            fragment = "&w=" + width + "&h=" + height;
        }

        if (orientation != Orientation.NONE) {
            fragment += "&o=" + orientation.val;
        }

        return !"".equals(fragment) ? fragment : null;
    }

    private String getLanguageFragment() {
        String fragment = null;
        String languages = Locale.getDefault().getLanguage();
        if (languages != null && !"".equals(languages)) {
                fragment = "&languages=" + WebUtils.urlEncode(languages);
        }
        return fragment;
    }

    private String getCustomParamsFragment() {
        String fragment = null;
        Map<String, String> customParams = request.getCustomParameters();
        if (customParams.size() > 0) {
            StringBuilder sb = new StringBuilder(customParams.size() * 10);
            for (Map.Entry<String, String> entry : customParams.entrySet()) {
                sb.append('&')
                    .append(WebUtils.urlEncode(entry.getKey()))
                    .append('=')
                    .append(WebUtils.urlEncode(entry.getValue()));
            }
            fragment = sb.toString();
        }
        return fragment;
    }
}
