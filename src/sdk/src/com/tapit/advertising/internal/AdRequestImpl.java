package com.tapit.advertising.internal;

import com.tapit.advertising.TapItAdRequest;
import com.tapit.adview.Utils;
import com.tapit.vastsdk.TVASTAdsRequest;
import com.tapit.adview.AdRequest;

import java.util.*;

public final class AdRequestImpl implements TapItAdRequest {

    private final String zone;
    private final boolean isTestMode;
    private final List<String> keywords;
    private final boolean isLocationTrackingEnabled;
    private final PlacementType placementType;
    private final Map<String, String> customParameters;


    public AdRequestImpl(String zone) {
        this(new BuilderImpl(zone));
    }

    private AdRequestImpl(Builder builder) {
        zone = builder.getZone();
        isTestMode = builder.isTestMode();
        keywords = builder.getKeywords();
        isLocationTrackingEnabled = builder.isLocationTrackingEnabled();
        placementType = builder.getPlacementType();
        customParameters = builder.getCustomParameters();
    }


    @Override
    public String getZone() {
        return zone;
    }

    @Override
    public PlacementType getPlacementType() {
        return placementType;
    }

    @Override
    public List<String> getKeywords() {
        return keywords;
    }

    @Override
    public boolean isTestMode() {
        return isTestMode;
    }

    @Override
    public boolean isLocationTrackingEnabled() {
        return isLocationTrackingEnabled;
    }

    @Override
    public Map<String, String> getCustomParameters() {
        return customParameters;
    }

    /**
     * Builder object used to generate {@link AdRequestImpl} objects.
     *
     * <h3>Simple Example:</h3>
     * <pre>
     *     AdRequestImpl request = AdRequestImpl.Builder("YOUR_ZONE_ID").getTapItAdRequest();
     * </pre>
     *
     * <h3>Advanced Example:</h3>
     * <pre>
     *     AdRequestImpl.Builder builder = AdRequestImpl.Builder("YOUR_ZONE_ID")
     *                                              // enable test mode during development
     *                                              .setTestMode(true)
     *
     *                                              // allow gps data to be used in ad request
     *                                              .setLocationTrackingEnabled(true)
     *
     *                                              // add relevant keywords to improve ad relevance
     *                                              .setKeywords(listOfKeywords);
     *     AdRequestImpl request = builder.getTapItAdRequest();
     *     // pass request to ad class...
     * </pre>
     */
    public static final class BuilderImpl implements Builder {
        private String zone;
        private boolean isTestMode = false;
        private boolean isLocationTrackingEnabled = false;
        private List<String> keywords = Collections.emptyList();
        private PlacementType placementType = null;
        private Map<String, String> customParameters = Collections.emptyMap();

        public BuilderImpl(String zone) {
            this.zone = zone;
        }

        @Override
        public TapItAdRequest getTapItAdRequest() {
            return new AdRequestImpl(this);
        }

        @Override
        public String getZone() {
            return zone;
        }

        @Override
        public Map<String, String> getCustomParameters() {
            return Collections.unmodifiableMap(customParameters);
        }

        @Override
        public Builder setCustomParameters(Map<String, String> customParameters) {
            if (customParameters == null) {
                this.customParameters = Collections.emptyMap();
            }
            else {
                this.customParameters = new HashMap<String, String>(customParameters);
            }
            return this;
        }

        @Override
        public boolean isLocationTrackingEnabled() {
            return isLocationTrackingEnabled;
        }

        @Override
        public Builder setLocationTrackingEnabled(boolean locationTrackingEnabled) {
            isLocationTrackingEnabled = locationTrackingEnabled;
            return this;
        }

        @Override
        public boolean isTestMode() {
            return isTestMode;
        }

        @Override
        public Builder setTestMode(boolean testMode) {
            isTestMode = testMode;
            return this;
        }

        @Override
        public List<String> getKeywords() {
            return Collections.unmodifiableList(keywords);
        }

        @Override
        public Builder setKeywords(List<String> keywords) {
            if (keywords == null) {
                this.keywords = Collections.emptyList();
            }
            else {
                this.keywords = new ArrayList<String>(keywords);
            }
            return this;
        }

        @Override
        public PlacementType getPlacementType() {
            return placementType;
        }

        @Override
        public Builder setPlacementType(PlacementType placementType) {
            this.placementType = placementType;
            return this;
        }
    }

    /**
     * Generates a new ad request object that is compatible w/ the under lying
     * implementation.  This is bridge code that will hopefully be phased out...
     * @param request the TapItAdRequest object to translate
     * @return a implementation specific instance.
     */
    public static AdRequest asImplAdRequest(final TapItAdRequest request) {
        AdRequest adRequest = new AdRequest(request.getZone());
        Map<String, String> cparms = new HashMap<String, String>(request.getCustomParameters());
        if (request.isTestMode()) {
            cparms.put("mode", "test");
        }

        adRequest.setAutomaticLocationTrackingEnabled(request.isLocationTrackingEnabled());

        if (!request.getKeywords().isEmpty()) {
            cparms.put("keywords", Utils.join(request.getKeywords(), ","));
        }

        if (request.getPlacementType() != null) {
            cparms.put("videotype", request.getPlacementType().toString());
        }
        adRequest.setCustomParameters(cparms);

        return adRequest;
    }

    /**
     * Generates a new ad request object that is compatible w/ underlying TVAST
     * video implementation.  This is bridge code that will hopefully be phased out...
     * @param request the TapItAdRequest object to translate
     * @return a TVAST implementation specific instance.
     */
    public static TVASTAdsRequest asTVASTImplAdRequest(TapItAdRequest request) {
        TVASTAdsRequest tvastRequest = new TVASTAdsRequest(request.getZone());
        if (request.getPlacementType() != null) {
            tvastRequest.setRequestParameter("videotype", request.getPlacementType().toString());
        }
        tvastRequest.setRequestParameter("cid", request.getCustomParameters().get("cid"));
        tvastRequest.getCustomParameters().put("rtype",RequestType.VIDEO.getRequestType());
        return tvastRequest;
    }
}
