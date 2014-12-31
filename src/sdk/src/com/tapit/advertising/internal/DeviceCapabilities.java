package com.tapit.advertising.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.tapit.core.TapItLog;

import java.io.IOException;
import java.util.List;

public final class DeviceCapabilities {

    public static class AdvertisingInfo {
        private final String id;
        private final boolean isLimitAdTrackingEnabled;

        AdvertisingInfo(String id, boolean isLimitAdTrackingEnabled) {
            this.id = id;
            this.isLimitAdTrackingEnabled = isLimitAdTrackingEnabled;
        }

        public String getId() {
            return id;
        }

        public boolean isLimitAdTrackingEnabled() {
            return isLimitAdTrackingEnabled;
        }
    }
    private static final String TAG = "TapIt";

    /**
     * Check to see if device has a dialer program
     * @param context app context
     * @return true if Intent.ACTION_DIAL should succeed, false otherwise
     */
    public static boolean canMakePhonecalls(Context context) {

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        List<ResolveInfo> callAppsList = context.getPackageManager().queryIntentActivities(callIntent, 0);
        return callAppsList != null && !callAppsList.isEmpty();
    }


    /**
     * Check to see if the device can send SMS's and that app has permission to do so.
     * @param context app context
     * @return true if app has permissions and device is able to send SMS's, false otherwise
     */
    public static boolean canSendSMS(Context context) {
        //TODO implement me!
//        SmsManager manager = SmsManager.getDefault();
//        Utils.hasPermission(context, Manifest.permission.WRITE_SMS);
        return false;
    }

    public static boolean canCreateCalendarEvents(Context context) {
        //TODO implement me!
        return true;
    }

    public static interface AdvertisingInfoCallback {
        public void advertisingInfoReceived(AdvertisingInfo advertisingInfo);
    }

    /**
     * Event based method for fetching advertiser info.
     * @param context the app contex
     * @param callback the callback which will be notified when advertiser info is available
     */
    public static void fetchAdvertiserInfo(final Context context, final AdvertisingInfoCallback callback) {
        AsyncTask<Void, Void, AdvertisingInfo> asyncTask = new AsyncTask<Void, Void, AdvertisingInfo>() {

            @Override
            protected AdvertisingInfo doInBackground(Void... voids) {
                return getAdvertiserInfo(context);
            }

            @Override
            protected void onPostExecute(AdvertisingInfo advertisingInfo) {
                callback.advertisingInfoReceived(advertisingInfo);
            }
        };

        if(Build.VERSION.SDK_INT >= 11) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
        }
        else {
            asyncTask.execute((Void)null);
        }
    }

    /**
     * you must not call this method from the UI thread...
     */
    public static AdvertisingInfo getAdvertiserInfo(final Context context) {
        AdvertisingInfo adInfo = null;
        try {
            // this will fail if no google play services isn't installed
            Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");

            adInfo = WrapperForGooglePlayCompat.getAdvertiserInfo(context);
        } catch (ClassNotFoundException e) {
            // no google play services support
            TapItLog.i(TAG, "No Google Play Services support.");
        }

        return adInfo;
    }

    /**
     * The Class.forName test above should ensure that this class is only loaded if google play services are available
     */
    private static class WrapperForGooglePlayCompat {
        public static AdvertisingInfo getAdvertiserInfo(final Context context) {
            AdvertisingInfo adInfo = null;
            try {
                AdvertisingIdClient.Info adClientInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                adInfo = new AdvertisingInfo(adClientInfo.getId(), adClientInfo.isLimitAdTrackingEnabled());
            } catch (IOException e) {
                // Unrecoverable error connecting to Google Play services (e.g.,
                // the old version of the service doesn't support getting AdvertisingId).
                TapItLog.i(TAG, "No Google Play Services support(1).", e);
            } catch (GooglePlayServicesNotAvailableException e) {
                // Google Play services is not available entirely.
                TapItLog.i(TAG, "No Google Play Services support(2).", e);
            } catch (GooglePlayServicesRepairableException e) {
                TapItLog.i(TAG, "No Google Play Services support(3).", e);
            } catch (IllegalStateException e) {
                TapItLog.i(TAG, "No Google Play Services support(4).", e);
            }

            return adInfo;
        }
    }
}
