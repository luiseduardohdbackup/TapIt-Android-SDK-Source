package com.tapit.adview;

public enum MraidPlacementType {
    INLINE("inline"),
    INTERSTITIAL("interstitial");

    public final String value;

    private MraidPlacementType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
