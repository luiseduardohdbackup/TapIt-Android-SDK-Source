package com.tapit.adview;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Build;
import com.tapit.core.TapItLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * MRAID related enums/consts/utils
 */
public final class Mraid {
    private static final String TAG = "TapIt";

    static enum MraidEvent {
        READY("ready"),
        STATECHANGE("stateChange"),
        SIZECHANGE("sizeChange"),
        VIEWABLECHANGE("viewableChange"),
        ERROR("error");

        public final String value;

        private MraidEvent(String val) {
            this.value = val;
        }

        public static MraidEvent parse(String val) {
            for(MraidEvent state : MraidEvent.values()) {
                if(state.value.equalsIgnoreCase(val)) {
                    return state;
                }
            }
            return null;
        }

    }

    static enum MraidPlacementType {
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

    static enum MraidState {
      LOADING("loading"),
      DEFAULT("default"),
      RESIZED("resized"),
      EXPANDED("expanded"),
      HIDDEN("hidden");

      public final String value;

      private MraidState(String val) {
        this.value = val;
      }

      public static MraidState parse(String val) {
        for(MraidState state : MraidState.values()) {
          if(state.value.equalsIgnoreCase(val)) {
            return state;
          }
        }
        return null;
      }
    }

    static enum MraidCloseRegionPosition {
        TOP_LEFT("top-left"),
        TOP_CENTER("top-center"),
        TOP_RIGHT("top-right"), // DEFAULT
        CENTER("center"),
        BOTTOM_LEFT("bottom-left"),
        BOTTOM_CENTER("bottom-center"),
        BOTTOM_RIGTH("bottom-right");

        public final String value;

        MraidCloseRegionPosition(String val) {
            value = val;
        }

        public static MraidCloseRegionPosition parse(String val) {
            for(MraidCloseRegionPosition state : MraidCloseRegionPosition.values()) {
                if(state.value.equalsIgnoreCase(val)) {
                    return state;
                }
            }
            return null;
        }
    }

    static enum MraidOrientation {
        PORTRAIT("portrait", (Build.VERSION.SDK_INT >= 9)
                                ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
        LANDSCAE("landscape", (Build.VERSION.SDK_INT >= 9)
                                ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
        NONE("none", ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        public final String value;
        public final int orientation;

        MraidOrientation(String val, int orientation) {
            value = val;
            this.orientation = orientation;
        }

        public static MraidOrientation parse(String val) {
            for(MraidOrientation state : MraidOrientation.values()) {
                if(state.value.equalsIgnoreCase(val)) {
                    return state;
                }
            }
            return null;
        }
    }


    public static String getMraidJs(Context context) {
        return MRAID_JS;
//        StringBuilder sb = new StringBuilder();
//        AssetManager assetManager = context.getAssets();
//        InputStream is = null;
//        BufferedReader reader = null;
//        try {
//            is = assetManager.open("mraid.js");
//            reader = new BufferedReader(new InputStreamReader(is));
//            String tmp;
//            while((tmp = reader.readLine()) != null) {
//                sb.append(tmp);
//            }
//        } catch (IOException e) {
//            TapItLog.e(TAG, "failed to load mraid.js", e);
//        }
//        finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    TapItLog.e(TAG, "Failed to close reader", e);
//                }
//
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    TapItLog.e(TAG, "Failed to close input stream", e);
//                }
//            }
//        }
//        TapItLog.d(TAG, "MRAIDJS: " + sb.toString());
//        return sb.toString();
    }

//    private static final String MRAID_JS = "var mraid={getState:function(){return\"loading\"},listeners:[],addEventListener:function(e,t){this.listeners.push({eventName:e,callback:t})}};(function(e,t){function a(e,n){console.debug(\"fireEvent: \"+e+\"(\"+n+\")\");for(var i=0;i<r.length;i++){var s=r[i];if(s.eventName.toLowerCase()==e.toLowerCase()){console.debug(\"calling: \"+s.callback);if(n){s.callback.apply(t,n)}else{s.callback()}}}}function f(e,t,n){if(e!=\"log\"&&s.state==\"hidden\"){var r=\"Made a call to a disposed ad unit\";var i=e;a(\"error\",[r,i]);return}var f=\"NATIVECALL://\"+e;if(n){var l=e+\"-\"+o++;u[l]=n;t[\"__callback\"]=l}if(t){var c=true;for(var h in t){if(t.hasOwnProperty(h)){if(c){f+=\"?\";c=false}else{f+=\"&\"}f+=h+\"=\"+t[h]}}}var p=document.createElement(\"IFRAME\");p.setAttribute(\"src\",f);document.documentElement.appendChild(p);p.parentNode.removeChild(p);p=null}var n=/iphone|ipad|ipod/i.test(window.navigator.userAgent.toLowerCase());if(n){window.console={};console.log=function(e){f(\"log\",{message:e},t)};console.debug=console.info=console.warn=console.error=console.log;console.debug(\"console logging initialized\")}var r=e.listeners||[];var i={width:false,height:false,useCustomClose:false,isModal:true};var s={placementType:\"inline\",isVisible:false,state:\"loading\",screenWidth:false,screenHeight:false,x:false,y:false,height:false,width:false,supportedFeatures:[],maxWidth:false,maxHeight:false};e.getVersion=function(){console.debug(\"getVersion() => '2.0'\");return\"2.0\"};e.getPlacementType=function(){console.debug(\"getPlacement\");return f(\"getPlacementType\")};e.getState=function(){console.debug(\"getState: \"+s.state);return s.state};e.isViewable=function(){var e=s.isVisible;console.debug(\"isViewable() => \"+e);return s.isVisible};e.addEventListener=function(e,t){console.debug(\"addEventListener: \"+e+\", \"+t);r.push({eventName:e,callback:t})};e.removeEventListener=function(e,t){console.debug(\"removeEventListener\");for(var n=r.length-1;n>=0;n--){if(r[n].eventName==e&&(typeof t==\"undefined\"||r[n].callback==t)){console.debug(\"removing {\"+r[n].eventName+\", \"+r[n].callback+\"}\");r.splice(n,1)}}};e.close=function(){console.debug(\"close\");f(\"close\")};e.open=function(e){console.debug(\"open(\"+e+\")\");f(\"open\",{url:encodeURIComponent(e).replace(/%20/g,\"+\")})};e.setExpandProperties=function(e){var t=\"{\";for(var n in e){if(e.hasOwnProperty(n)){t+=\", \"+n+\": \"+e[n]}}t+=\"}\";console.debug(\"setExpandProperties: \"+t);if(\"width\"in e){i.width=e.width}if(\"height\"in e){i.height=e.height}if(\"useCustomClose\"in e){i.useCustomClose=e.useCustomClose}};e.getExpandProperties=function(){if(!i.width){i.width=s.maxWidth;i.height=s.maxHeight}var e={width:i.width,height:i.height,useCustomClose:i.useCustomClose,isModal:i.isModal};console.debug(\"getExpandProperties() => \"+JSON.stringify(e));return e};e.expand=function(t){console.debug(\"expand(\"+t+\")\");var n=e.getExpandProperties();if(typeof t!==\"undefined\"){n[\"url\"]=t}f(\"expand\",n)};e.useCustomClose=function(e){console.debug(\"useCustomClose(\"+e+\")\");i.useCustomClose=e;f(\"useCustomClose\",{useCustomClose:e})};var o=0;var u={};e._nativeResponse=function(e,t){if(typeof t!=\"undefined\"){console.debug(\"id defined!\");if(t in u){var n=u[t];delete u[t];n.apply(e)}}else{var r=\"data: \";for(var i in e){if(e.hasOwnProperty(i)){if(i in s){s[i]=e[i];r+=i+\": \"+e[i]+\" \"}else if(i==\"_fire_event_\"){var o=e[i];a(o.name,o.props)}}}console.debug(r)}};var l={x:0,y:0,width:0,height:0};var c={allowOrientationChange:true,forceOrientation:\"none\"};var h={width:0,height:0,offsetX:0,offsetY:0,customClosePosition:\"top-right\",allowOffscreen:true};e.getCurrentPosition=function(){var e={x:s.x,y:s.y,width:s.width,height:s.height};console.debug(\"getCurrentPosition(\"+JSON.stringify(e)+\")\");return e};e.getDefaultPosition=function(){console.debug(\"getDefaultPosition() => \"+JSON.stringify(l));return{x:l.x,y:l.y,width:l.width,height:l.height}};e.getScreenSize=function(){var e={width:s.screenWidth,height:s.screenHeight};console.debug(\"getScreenSize() => \"+JSON.stringify(e));return e};e.getOrientationProperties=function(){var e={allowOrientationChange:c.allowOrientationChange,forceOrientation:c.forceOrientation};console.debug(\"getOrientationProperties() => \"+JSON.stringify(e));return e};e.setOrientationProperties=function(e){console.debug(\"setOrientationProperties(\"+JSON.stringify(e)+\")\");if(\"allowOrientationChange\"in e){c.allowOrientationChange=e.allowOrientationChange}if(\"forceOrientation\"in e){c.forceOrientation=e.forceOrientation}f(\"setOrientationProperties\",c,t)};e.createCalendarEvent=function(e){console.debug(\"createCalendarEvent(\"+JSON.stringify(e)+\")\");f(\"createCalendarEvent\",e,t)};e.getMaxSize=function(){var e={width:s.maxWidth,height:s.maxHeight};console.debug(\"getMaxSize() => \"+JSON.stringify(e));return e};e.playVideo=function(e){console.debug(\"playVideo(\"+e+\")\");f(\"playVideo\",{url:e},t)};e.getResizeProperties=function(){console.debug(\"getResizeProperties() => \"+JSON.stringify(h));return h};e.setResizeProperties=function(e){console.debug(\"setResizeProperties(\"+JSON.stringify(e)+\")\");h.width=e.width;h.height=e.height;h.offsetX=e.offsetX;h.offsetY=e.offsetY;if(\"customClosePosition\"in e){h.customClosePosition=e.customClosePosition}if(\"allowOffscreen\"in e){h.allowOffscreen=e.allowOffscreen}};e.resize=function(){console.debug(\"resize()\");f(\"resize\",h)};e.storePicture=function(e){console.debug(\"storePicture(\"+e+\")\");f(\"storePicture\",{url:e},t)};e.supports=function(e){var t=s.supportedFeatures.indexOf(e)>-1;console.debug(\"supports(\"+e+\") => \"+t);return t}})(window.mraid)";
    private static final String MRAID_JS = "function isInteger(e){return typeof e===\"number\"&&e%1===0}var mraid={getState:function(){return\"loading\"},listeners:[],addEventListener:function(e,t){this.listeners.push({eventName:e,callback:t})}};(function(e,t){function a(e,n){console.debug(\"fireEvent: \"+e+\"(\"+n+\")\");for(var i=0;i<r.length;i++){var s=r[i];console.debug(\"checking event: \"+s.eventName);if(s.eventName.toLowerCase()==e.toLowerCase()){console.debug(\"calling: \"+s.callback);if(n){s.callback.apply(t,n)}else{s.callback()}}}}function f(e,t,n){if(e!=\"log\"&&s.state==\"hidden\"){var r=\"Made a call to a disposed ad unit\";var i=e;a(\"error\",[r,i]);return}var f=\"NATIVECALL://\"+e;if(n){var l=e+\"-\"+o++;u[l]=n;t[\"__callback\"]=l}if(t){var c=true;for(var h in t){if(t.hasOwnProperty(h)){if(c){f+=\"?\";c=false}else{f+=\"&\"}f+=h+\"=\"+encodeURIComponent(t[h])}}}var p=document.createElement(\"IFRAME\");p.setAttribute(\"src\",f);document.documentElement.appendChild(p);p.parentNode.removeChild(p);p=null}var n=/iphone|ipad|ipod/i.test(window.navigator.userAgent.toLowerCase());if(n){window.console={};console.log=function(e){f(\"log\",{message:e},t)};console.debug=console.info=console.warn=console.error=console.log;console.debug(\"console logging initialized\")}var r=e.listeners||[];var i={width:false,height:false,useCustomClose:false,isModal:true};var s={placementType:\"inline\",isVisible:false,state:\"loading\",height:false,width:false,screenWidth:false,screenHeight:false,x:false,y:false};e.getVersion=function(){console.debug(\"getVersion => 2.0\");return\"2.0\"};e.getPlacementType=function(){console.debug(\"getPlacement => \"+s.placementType);return s.placementType};e.getState=function(){console.debug(\"getState => \"+s.state);return s.state};e.isViewable=function(){var e=s.isVisible;console.debug(\"isViewable is \"+(e?\"Viewable\":\"NOT Viewable\"));return s.isVisible};e.addEventListener=function(e,t){console.debug(\"addEventListener: \"+e+\", \"+t);r.push({eventName:e,callback:t})};e.removeEventListener=function(e,t){console.debug(\"removeEventListener(\"+e+\")\");for(var n=r.length-1;n>=0;n--){if(r[n].eventName==e&&(typeof t==\"undefined\"||r[n].callback==t)){console.debug(\"removing {\"+r[n].eventName+\", \"+r[n].callback+\"}\");r.splice(n,1)}}};e.close=function(){console.debug(\"close\");f(\"close\")};e.open=function(e){console.debug(\"open(\"+e+\")\");f(\"open\",{url:e})};e.setExpandProperties=function(e){var t=\"{\";var n=0;for(var r in e){if(e.hasOwnProperty(r)){if(n==0){t+=r+\": \"+e[r];n++}else{t+=\", \"+r+\": \"+e[r]}}}t+=\"}\";console.debug(\"setExpandProperties: \"+t);if(\"width\"in e){i.width=e.width}if(\"height\"in e){i.height=e.height}if(\"useCustomClose\"in e){i.useCustomClose=e.useCustomClose}};e.getExpandProperties=function(){if(!i.width){i.width=s.screenWidth;i.height=s.screenHeight}var e={width:i.width,height:i.height,useCustomClose:i.useCustomClose,isModal:i.isModal};console.debug(\"getExpandProperties => \"+JSON.stringify(e));return e};e.expand=function(t){console.debug(\"expand(\"+t+\")\");var n=e.getExpandProperties();if(typeof t!==\"undefined\"){n[\"url\"]=t}f(\"expand\",n)};e.useCustomClose=function(e){console.debug(\"useCustomClose(\"+e+\")\");i.useCustomClose=e;f(\"useCustomClose\",{useCustomClose:e})};var o=0;var u={};e._nativeResponse=function(e,t){if(typeof t!=\"undefined\"){console.debug(\"id defined!\");if(t in u){var n=u[t];delete u[t];n.apply(e)}}else{var r=\"data: \";for(var i in e){if(e.hasOwnProperty(i)){debugger;if(i in s){s[i]=e[i];r+=i+\": \"+e[i]+\" \"}else if(i==\"_fire_event_\"){var o=e[i];console.debug(\"trying to fire event named: \"+o.name+\" with props \"+o.props);a(o.name,o.props)}}}console.debug(r)}};var l={x:0,y:0,width:0,height:0};var c={allowOrientationChange:true,forceOrientation:\"none\"};var h={width:false,height:false,offsetX:false,offsetY:false,customClosePosition:\"top-right\",allowOffscreen:false};e.getOrientationProperties=function(){var e={allowOrientationChange:c.allowOrientationChange,forceOrientation:c.forceOrientation};console.debug(\"getOrientationProperties => \"+JSON.stringify(e));return e};e.getCurrentPosition=function(){var e={x:s.x,y:s.y,width:s.width,height:s.height};console.debug(\"getCurrentPosition => \"+JSON.stringify(e));return e};e.setCurrentPosition=function(t){console.debug(\"setCurrentPosition\");var n=e.getCurrentPosition();if(\"x\"in t){s.x=t.x;l.x=t.x}if(\"y\"in t){s.y=t.y;l.y=t.y}if(\"height\"in t){s.height=t.height;l.height=t.height}if(\"width\"in t){s.width=t.width;l.width=t.width}var i=e.getCurrentPosition();if(n.width===i.width&&n.height===i.height){return}var o=r[\"sizeChange\"];if(o){var u=s.width;var a=s.height;for(var f=0;f<o.length;++f){o[f](u,a)}}};e.getDefaultPosition=function(){var e={x:l.x,y:l.y,width:l.width,height:l.height};console.debug(\"getDefaultPosition => \"+JSON.stringify(e));return e};e.getScreenSize=function(){var e={width:s.screenWidth,height:s.screenHeight};console.debug(\"getScreenSize => \"+JSON.stringify(e));return e};e.setOrientationProperties=function(e){console.debug(\"setOrientationProperties(\"+JSON.stringify(e)+\")\");if(\"allowOrientationChange\"in e){c.allowOrientationChange=e.allowOrientationChange}if(\"forceOrientation\"in e){c.forceOrientation=e.forceOrientation}f(\"setOrientationProperties\",c,t)};e.createCalendarEvent=function(e){f(\"createCalendarEvent\",e,t)};var p=e.maxSize={width:0,height:0};e.getMaxSize=function(){console.debug(\"getMaxSize: width:\"+p.width+\" height:\"+p.height);return p};e.setMaxSize=function(e){p.height=e.height;p.width=e.width;console.debug(\"setMaxSize \"+p.width+\" \"+p.height)};e.getResizeProperties=function(){if(h.width==false||h.height==false){e.fireErrorEvent(\"Can't get resize properties for frame without setting resize properties first\",\"getResizeProperties\")}else{console.debug(\"getResizeProperties => \"+JSON.stringify(h));return h}};e.playVideo=function(e){console.debug(\"playVideo(\"+e+\")\");f(\"playVideo\",{url:e},t)};e.resize=function(){console.debug(\"resize\");var t=e.getState();if(t==\"expanded\"){var n=\"Can't resize an expanded ad unit\";var r=\"resize\";a(\"error\",[n,r])}else{s.height=h.height;s.width=h.width;s.state=\"resized\";f(\"resize\",{width:s.width,height:s.height})}};e.setResizeProperties=function(t){console.debug(\"setResizeProperties \"+t.height+\" \"+t.width+\" \"+t.offsetX+\" \"+t.offsetY+\" \"+t.allowOffscreen.toString());if(!isInteger(t.height)||!isInteger(t.width)||!isInteger(t.offsetX)||!isInteger(t.offsetY)||t.height<=50&&t.width<=50||t.height>p.height||t.width>p.width){h={width:false,height:false,offsetX:false,offsetY:false,customClosePosition:\"top-right\",allowOffscreen:false};e.fireErrorEvent(\"Invalid data passed to resize properties\",\"setResizeProperties\")}else{h={width:t.width,height:t.height,offsetX:t.offsetX,offsetY:t.offsetY,customClosePosition:t.customClosePosition,allowOffscreen:t.allowOffscreen}}};e.storePicture=function(e){console.debug(\"storePicture(\"+e+\")\");f(\"storePicture\",{url:e},t)};var d=e.FEATURES={SMS:\"sms\",PHONE:\"tel\",CALENDAR:\"calendar\",STORE_PICTURE:\"storePicture\",INLINE_VIDEO:\"inlineVideo\"};var v={};e.setSupports=function(e,t){v[e]=t;console.debug(e+\" is being set to \"+t)};e.supports=function(e){var t=v[e];console.debug(\"supports(\"+e+\") => \"+t);return t};e.fireErrorEvent=function(e,t){console.log(\"fireErrorEvent handler:\"+e+\" action:\"+t);var n=r[EVENTS.ERROR];if(n){for(var i=0;i<n.length;++i){n[i](e,t)}}}})(window.mraid)";
}
