package com.tapit.adview;

public enum MraidState {
  LOADING("loading"),
  DEFAULT("default"),
  RESIZED("resized"),
  EXPANDED("expanded"),
  HIDDEN("hidden");

  public final String value;

  private MraidState(String val) {
    this.value = val;
  }

  public static MraidState marshalMraidState(String val) {
    for(MraidState state : MraidState.values()) {
      if(state.value.equalsIgnoreCase(val)) {
        return state;
      }
    }
    return null;
  }
}
