package com.tapit.adview;

import java.util.Map;

public interface IMraidCommand {
  public void execute(Map<String, String> params, AdViewCore adView);
}
