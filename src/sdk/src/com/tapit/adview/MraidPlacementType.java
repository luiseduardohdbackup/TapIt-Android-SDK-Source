package com.tapit.adview;

public enum MraidPlacementType {
    INLINE("inline"),
    INTERSTITIAL("interstitial");

    public final String name;

    private MraidPlacementType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
