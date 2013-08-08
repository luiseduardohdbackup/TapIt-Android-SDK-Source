package com.tapit.adview;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

public enum MraidCommand implements IMraidCommand {
    CLOSE("close", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            // tell adView to close
            TILog.d("Close called!");
            adView.mraidClose();
        }
    }),

    EXPAND("expand", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            // tell adview to expand to full screen
            TILog.d("Expand command called with params: " + params);
            int height = Integer.parseInt(params.get("height"));
            int width = Integer.parseInt(params.get("width"));
            boolean useCustomClose = "true".equalsIgnoreCase(params.get("useCustomClose"));
            adView.useCustomCloseButton(useCustomClose);
            boolean isModal = "true".equalsIgnoreCase(params.get("isModal"));
            String twoPartCreativeUrl = params.get("url");
            adView.resize(height, width, isModal, twoPartCreativeUrl);
            adView.setMraidState(MraidState.EXPANDED);
            adView.syncMraidState();
            adView.fireMraidEvent(MraidEvent.STATECHANGE, adView.getMraidState().value);
        }
    }),

    OPEN("open", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            // tell adview to open url in in-app browser
            String url = params.get("url");
            TILog.d("open(" + url + ")");
            adView.open(url);
        }
    }),

    CUSTOM_CLOSE_BUTTON("useCustomClose", new IMraidCommand() {
        @Override
        public void execute(Map<String, String> params, AdViewCore adView) {
            // tell adview if we should render a close button
            boolean useCustomClose = "true".equalsIgnoreCase(params.get("useCustomClose"));
            adView.useCustomCloseButton(useCustomClose);
        }
    })

//  ,
//  SET_ORIENTATION_PROPERTIES("setOrientation", new IMraidCommand() {
//    @Override
//    public void execute(Map<String, String> params, AdViewCore adView) {
//      //To change body of implemented methods use File | Settings | File Templates.
//    }
//  })
    ;

    public final String command;
    private final IMraidCommand commandListener;

    private static final Pattern QUESTION_MARK_PATTERN = Pattern.compile("\\?");
    private static final Pattern DBL_SLASH_PATTERN = Pattern.compile("//");

    private MraidCommand(String commandName, IMraidCommand commandCode) {
        command = commandName;
        commandListener = commandCode;
    }

    public void execute(Map<String, String> params, AdViewCore adView) {
        commandListener.execute(params, adView);
    }

    public static MraidCommand marshalMraidCommand(String commandName) {
        for (MraidCommand cmd : MraidCommand.values()) {
            if (cmd.command.equalsIgnoreCase(commandName)) {
                return cmd;
            }
        }

        return null;
    }

    public static void routeRequest(String url, AdViewCore adView) {
        // parse string
        String parts[] = QUESTION_MARK_PATTERN.split(url, 2);
        String commandName = parts[0];
        Map<String, String> params = Collections.<String, String>emptyMap();
        if(parts.length == 2) {
            // has query string
            try {
                params = Utils.parseUrlParams(url);
            } catch (UnsupportedEncodingException e) {
                TILog.e("Failed to parse native MRAID QS params: " + url);
                return;
            }
        }
        parts = DBL_SLASH_PATTERN.split(commandName);
        if (parts.length != 2) {
            TILog.e("Failed to parse native MRAID command: " + url);
            return;
        }
        commandName = parts[1];

        // fire off command
        TILog.d("Command: " + commandName + "(" + params + ")");
        MraidCommand command = marshalMraidCommand(commandName);
        if (command != null) {
            command.execute(params, adView);
        }
    }
}
