package com.tapit.adview;

public enum MraidEvent {
  READY("ready"),
  STATECHANGE("stateChange"),
  SIZECHANGE("sizeChange"),
  VIEWABLECHANGE("viewableChange"),
  ERROR("error");

  public final String value;

  private MraidEvent(String val) {
    this.value = val;
  }

  public static MraidEvent marshalMraidEvent(String val) {
    for(MraidEvent state : MraidEvent.values()) {
      if(state.value.equalsIgnoreCase(val)) {
        return state;
      }
    }
    return null;
  }

}
