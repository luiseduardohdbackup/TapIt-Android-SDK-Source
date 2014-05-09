package com.tapit.advertising;

import java.util.List;
import java.util.Map;

/**
 * Request object used to hold request configuration details. Use {@link Builder} to generate instances. *
 */
public interface TapItAdRequest {
    static enum PlacementType {
        ALL("all"),
        PRE_ROLL("pre-roll"),
        MID_ROLL("mid-roll"),
        POST_ROLL("post-roll");

        private final String name;

        PlacementType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public String getZone();

    /**
     * This property is specific to video interstitial ads.
     * @return placement type for video ad request
     */
    public PlacementType getPlacementType();

    public List<String> getKeywords();

    public boolean isTestMode();

    public boolean isLocationTrackingEnabled();

    public Map<String, String> getCustomParameters();

    /**
     * Builder object used to generate customized {@link TapItAdRequest} objects.
     *
     * <h3>Example Usage</h3>
     * <pre>
     * TapItAdRequest.Builder builder = TapItAdvertising.getTapItAdRequestBuilder("YOUR_ZONE_ID")
     *                                          // enable test mode during development
     *                                          .setTestMode(true)
     *
     *                                          // allow gps data to be used in ad request
     *                                          .setLocationTrackingEnabled(true)
     *
     *                                          // add relevant keywords to improve ad relevance
     *                                          .setKeywords(listOfKeywords);
     * TapItAdRequest request = builder.getTapItAdRequest();
     * </pre>
     */
    public static interface Builder {

        public TapItAdRequest getTapItAdRequest();

        public String getZone();

        public Map<String, String> getCustomParameters();

        public Builder setCustomParameters(Map<String, String> customParameters);

        public boolean isLocationTrackingEnabled();

        public Builder setLocationTrackingEnabled(boolean locationTrackingEnabled);

        public boolean isTestMode();

        public Builder setTestMode(boolean testMode);

        public List<String> getKeywords();

        public Builder setKeywords(List<String> keywords);

        public PlacementType getPlacementType();

        public Builder setPlacementType(PlacementType placementType);
    }

}
