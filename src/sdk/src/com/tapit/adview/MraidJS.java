package com.tapit.adview;

public class MraidJS {
    public static final String MRAID_JS = "var mraid = window.mraid || {\n" +
            "    getState: function() {return \"loading\"},\n" +
            "    listeners: [],\n" +
            "    addEventListener: function(eventName, callback) { this.listeners.push({eventName:eventName, callback:callback}); }\n" +
            "};\n" +
            "\n" +
            "(function(mraid, undefined) {\n" +
            "\n" +
            "    var isIOS = (/iphone|ipad|ipod/i).test(window.navigator.userAgent.toLowerCase());\n" +
            "    if (isIOS) {\n" +
            "        window.console = {};\n" +
            "        console.log = function(message) {\n" +
            "            nativeExecute(\"log\", {message:message}, undefined);\n" +
            "        };\n" +
            "        console.debug = console.info = console.warn = console.error = console.log;\n" +
            "        console.debug(\"console logging initialized\");\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    var listeners = mraid.listeners || [];\n" +
            "    var expandProperties = {\n" +
            "        width : false,\n" +
            "        height : false,\n" +
            "        useCustomClose : false,\n" +
            "        isModal : true // read-only\n" +
            "    };\n" +
            "\n" +
            "    var nativeData = {\n" +
            "            placementType: \"inline\", // \"inline\", \"interstitial\"\n" +
            "        isVisible: false,\n" +
            "        state: \"loading\", // \"loading\", \"default\", \"expanded\", \"resized\", or \"hidden\"\n" +
            "        height: false,\n" +
            "        width: false\n" +
            "    };\n" +
            "\n" +
            "    mraid.getVersion = function() { console.debug(\"getVersion\"); return \"1.0\"; };\n" +
            "\n" +
            "    mraid.getPlacementType = function() { console.debug(\"getPlacement\"); return nativeExecute(\"getPlacementType\"); };\n" +
            "\n" +
            "    mraid.getState = function() { console.debug(\"getState: \" + nativeData.state); return nativeData.state; };\n" +
            "\n" +
            "    mraid.isViewable = function() {\n" +
            "        var isViewable = nativeData.isVisible;\n" +
            "        console.debug(\"isViewable is \" + (isViewable ? \"Viewable\" : \"NOT Viewable\"));\n" +
            "        return nativeData.isVisible; };\n" +
            "\n" +
            "    mraid.addEventListener = function(eventName, callback) {\n" +
            "        // available events: \"ready\", \"error\", \"stateChange\", \"viewableChange\", \"sizeChange\"\n" +
            "        console.debug(\"addEventListener: \" + eventName + \", \" + callback);\n" +
            "        listeners.push({eventName: eventName, callback: callback});\n" +
            "    };\n" +
            "\n" +
            "    mraid.removeEventListener = function(eventName, callback) {\n" +
            "        console.debug(\"removeEventListener\");\n" +
            "        for(var i = listeners.length-1; i >= 0; i--) {\n" +
            "            if(listeners[i].eventName == eventName\n" +
            "                && (typeof(callback) == \"undefined\" || listeners[i].callback == callback)) {\n" +
            "                console.debug(\"removing {\" + listeners[i].eventName + \", \" + listeners[i].callback + \"}\");\n" +
            "                listeners.splice(i,1);\n" +
            "            }\n" +
            "        }\n" +
            "    };\n" +
            "\n" +
            "    mraid.close = function() {\n" +
            "        console.debug(\"close\");\n" +
            "        nativeExecute(\"close\");\n" +
            "    };\n" +
            "\n" +
            "    mraid.open = function(url) {\n" +
            "        console.debug(\"open\");\n" +
            "        nativeExecute(\"open\", {url: url});\n" +
            "    };\n" +
            "\n" +
            "    mraid.setExpandProperties = function(props) {\n" +
            "        var propsStr = \"{\";\n" +
            "        for(var p in props) {\n" +
            "            if(props.hasOwnProperty(p)) {\n" +
            "                propsStr += \", \" + p + \": \" + props[p];\n" +
            "            }\n" +
            "        }\n" +
            "        propsStr += \"}\";\n" +
            "        console.debug(\"setExpandProperties: \" + propsStr);\n" +
            "        if('width' in props) {\n" +
            "            expandProperties.width = props.width;\n" +
            "        }\n" +
            "\n" +
            "        if('height' in props) {\n" +
            "            expandProperties.height = props.height;\n" +
            "        }\n" +
            "\n" +
            "        if('useCustomClose' in props) {\n" +
            "            expandProperties.useCustomClose = props.useCustomClose;\n" +
            "        }\n" +
            "    };\n" +
            "\n" +
            "    mraid.getExpandProperties = function() {\n" +
            "        console.debug(\"getExpandProperties\");\n" +
            "        if(!expandProperties.width) {\n" +
            "            expandProperties.width = nativeData.width;\n" +
            "            expandProperties.height = nativeData.height;\n" +
            "        }\n" +
            "\n" +
            "        return {\n" +
            "            width : expandProperties.width,\n" +
            "            height : expandProperties.height,\n" +
            "            useCustomClose : expandProperties.useCustomClose,\n" +
            "            isModal : expandProperties.isModal\n" +
            "        };\n" +
            "    };\n" +
            "\n" +
            "    mraid.expand = function(url) {\n" +
            "        console.debug(\"expand\");\n" +
            "        var params = mraid.getExpandProperties();\n" +
            "        if(typeof(url) !== 'undefined') {\n" +
            "            params['url'] = url;\n" +
            "        }\n" +
            "        nativeExecute(\"expand\", params);\n" +
            "    };\n" +
            "\n" +
            "    mraid.useCustomClose = function(useCustomClose) {\n" +
            "        console.debug(\"useCustomClose(\" + useCustomClose + \")\");\n" +
            "        expandProperties.useCustomClose = useCustomClose;\n" +
            "        nativeExecute(\"useCustomClose\", {useCustomClose:useCustomClose});\n" +
            "    };\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "    // code to handle comms between native app and js\n" +
            "\n" +
            "    var unique = 0;\n" +
            "    var responseQueue = {};\n" +
            "\n" +
            "    mraid._nativeResponse = function(data, id) {\n" +
            "        if(typeof(id) != 'undefined') {\n" +
            "            console.debug(\"id defined!\");\n" +
            "            if(id in responseQueue) {\n" +
            "//                log(\"found id: \" + id);\n" +
            "                var fn = responseQueue[id];\n" +
            "                delete responseQueue[id];\n" +
            "                fn.apply(data);\n" +
            "            }\n" +
            "        }\n" +
            "        else {\n" +
            "            var msg = \"data: \";\n" +
            "            // native app is relaying some sort of state change\n" +
            "            for(var param in data) {\n" +
            "                if(data.hasOwnProperty(param)) {\n" +
            "                    debugger;\n" +
            "                    if(param in nativeData) {\n" +
            "                        nativeData[param] = data[param];\n" +
            "                        msg += param + \": \" + data[param] + \" \";\n" +
            "                    }\n" +
            "                    else if(param == \"_fire_event_\") {\n" +
            "                        var evt = data[param];\n" +
            "                        msg += \"_fire_event_: \" + evt.name;\n" +
            "                        console.debug(\"trying to fire event: \" + evt.name);\n" +
            "                        fireEvent(evt.name, evt.props);\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "            console.debug(msg)\n" +
            "        }\n" +
            "    };\n" +
            "\n" +
            "    function fireEvent(event, params) {\n" +
            "        console.debug(\"fireEvent: \" + event + \"(\" + params + \")\");\n" +
            "        for(var i = 0; i < listeners.length; i++) {\n" +
            "            var lstnr = listeners[i];\n" +
            "            console.debug(\"checking event: \" + lstnr.eventName);\n" +
            "            if(lstnr.eventName.toLowerCase() == event.toLowerCase()) {\n" +
            "                console.debug(\"calling: \" + lstnr.callback);\n" +
            "                if(params) {\n" +
            "                    lstnr.callback.apply(undefined, params);\n" +
            "                }\n" +
            "                else {\n" +
            "                    lstnr.callback();\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    function nativeExecute(method, props, callback) {\n" +
            "        if(method != \"log\" && nativeData.state == \"hidden\") {\n" +
            "            var message = \"Made a call to a disposed ad unit\";\n" +
            "            var action = method;\n" +
            "            fireEvent(\"error\", [message, action]);\n" +
            "            return;\n" +
            "        }\n" +
            "        var url = \"NATIVECALL://\" + method;\n" +
            "        if(callback) {\n" +
            "            // expecting results back from the native call...\n" +
            "            var callId = method + \"-\" + unique++;\n" +
            "            responseQueue[callId] = callback;\n" +
            "            props['__callback'] = callId;\n" +
            "        }\n" +
            "        if(props) {\n" +
            "            var first = true;\n" +
            "            for(var p in props) {\n" +
            "                if(props.hasOwnProperty(p)) {\n" +
            "                    if(first) {\n" +
            "                        url += \"?\";\n" +
            "                        first = false;\n" +
            "                    }\n" +
            "                    else {\n" +
            "                        url += \"&\";\n" +
            "                    }\n" +
            "                    url += p + \"=\" + props[p];\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "//        console.debug(\"calling command: \" + url);\n" +
            "        var iframe = document.createElement(\"IFRAME\");\n" +
            "        iframe.setAttribute(\"src\", url);\n" +
            "        document.documentElement.appendChild(iframe);\n" +
            "        iframe.parentNode.removeChild(iframe);\n" +
            "        iframe = null;\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    /***********************************************\n" +
            "     * MRAID 2.0 functionality\n" +
            "     ***********************************************/\n" +
            "\n" +
            "    var screenSize = {\n" +
            "        width:0,\n" +
            "        height:0\n" +
            "    };\n" +
            "    var defaultPosition = {\n" +
            "        x:0,\n" +
            "        y:0,\n" +
            "        width:0,\n" +
            "        height:0\n" +
            "    };\n" +
            "    var currentPosition = {\n" +
            "        x:0,\n" +
            "        y:0,\n" +
            "        width:0,\n" +
            "        height:0\n" +
            "    };\n" +
            "    var orientationProperties = {\n" +
            "        allowOrientationChange : true,\n" +
            "        forceOrientation : \"none\" // portrait, landscape, none\n" +
            "    };\n" +
            "    var resizeProperties = {\n" +
            "        width : 0,\n" +
            "        height : 0,\n" +
            "        offsetX : 0,\n" +
            "        offsetY : 0,\n" +
            "        customClosePosition : \"top-right\", // \"top-left\", \"top-right\", \"center\", \"bottom-left\", \"bottom-right,\" \"top-center,\" or \"bottom-center\"\n" +
            "        allowOffscreen : false\n" +
            "    };\n" +
            "\n" +
            "    mraid.getOrientationProperties = function() {\n" +
            "        console.debug(\"getOrientationProperties\");\n" +
            "        return {\n" +
            "            allowOrientationChange: orientationProperties.allowOrientationChange,\n" +
            "            forceOrientation: orientationProperties.forceOrientation\n" +
            "        };\n" +
            "    };\n" +
            "\n" +
            "    mraid.getCurrentPosition = function() {\n" +
            "        console.debug(\"getCurrentPosition\");\n" +
            "        return {\n" +
            "            x: currentPosition.x,\n" +
            "            y: currentPosition.y,\n" +
            "            width: currentPosition.width,\n" +
            "            height: currentPosition.height\n" +
            "        };\n" +
            "    };\n" +
            "    mraid.getDefaultPosition = function() {\n" +
            "        console.debug(\"getDefaultPosition: \" + defaultPosition);\n" +
            "        return {\n" +
            "            x: defaultPosition.x,\n" +
            "            y: defaultPosition.y,\n" +
            "            width: defaultPosition.width,\n" +
            "            height: defaultPosition.height\n" +
            "        };\n" +
            "    };\n" +
            "    mraid.getScreenSize = function() {\n" +
            "        console.debug(\"getScreenSize\");\n" +
            "        return {\n" +
            "            width: screenSize.width,\n" +
            "            height: screenSize.height\n" +
            "        };\n" +
            "    };\n" +
            "\n" +
            "    mraid.setOrientationProperties = function(props) {\n" +
            "        console.debug(\"setOrientationProperties\");\n" +
            "        if('allowOrientationChange' in props) {\n" +
            "            orientationProperties.allowOrientationChange = props.allowOrientationChange;\n" +
            "        }\n" +
            "\n" +
            "        if('forceOrientation' in props) {\n" +
            "            orientationProperties.forceOrientation = props.forceOrientation;\n" +
            "        }\n" +
            "        nativeExecute(\"setOrientationProperties\", orientationProperties, undefined);\n" +
            "    };\n" +
            "\n" +
            "    mraid.createCalendarEvent = function() { console.debug(\"createCalendarEvent\"); };\n" +
            "    mraid.getMaxSize = function() { console.debug(\"getMaxSize\"); };\n" +
            "    mraid.getResizeProperties = function() { console.debug(\"getResizeProperties\"); };\n" +
            "    mraid.playVideo = function() { console.debug(\"playVideo\"); };\n" +
            "    mraid.resize = function() { console.debug(\"resize\"); };\n" +
            "    mraid.setResizeProperties = function() { console.debug(\"setResizeProperties\"); };\n" +
            "    mraid.storePicture = function() { console.debug(\"storePicture\"); };\n" +
            "    mraid.supports = function(feature) { console.debug(\"supports(\" + feature + \")\"); return false; };\n" +
            "\n" +
            "} (window.mraid));\n";
}
