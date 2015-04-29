package com.tapit.advertising.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.tapit.core.TapItLog;

public class TapItAdActivity extends Activity {
    private static final String TAG = "TapIt";
    public static final String CONTENT_WRAPPER_EXTRA = "AdActivityContentWrapper";

    private InterstitialBaseView contentView = null;
    private AdActivityContentWrapper adActivityContentWrapper = null;
    private boolean isContentStarted = false;

    /**
     * Convenience method for firing off the TapItAdActivity
     * @param context the calling context
     * @param wrapper the wrapper used to pass data to the activity
     */
    public static void startActivity(Context context, AdActivityContentWrapper wrapper) {
        startActivity(context, wrapper, null);
    }

    /**
     * Convenience method for firing off the TapItAdActivity
     * @param context the calling context
     * @param wrapper the wrapper used to pass data to the activity
     */
    public static void startActivity(Context context, AdActivityContentWrapper wrapper,
                                     Bundle extras) {
        Intent i = new Intent(context, TapItAdActivity.class);
        if (extras != null) {
            i.putExtras(extras);
        }
        Parcelable wrapperSharable = new Sharable<AdActivityContentWrapper>(
                wrapper, TapItAdActivity.CONTENT_WRAPPER_EXTRA);
        i.putExtra(TapItAdActivity.CONTENT_WRAPPER_EXTRA, wrapperSharable);
        context.startActivity(i);
    }

    /**
     * Used to programmatically close TapItAdActivity... example usage includes a close
     * button, or when a video ad finishes playing.
     */
    public void close() {
        adActivityContentWrapper.stopContent();
        finish();
        adActivityContentWrapper.done();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        TapItLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // defaults, override in AdActivityContentWrapper.getContentView(...)
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        Bundle extras = getIntent().getExtras();
        Sharable<AdActivityContentWrapper> wrapperSharable = extras.getParcelable(CONTENT_WRAPPER_EXTRA);
        adActivityContentWrapper = wrapperSharable.obj();

        contentView = new InterstitialBaseView(this);
        contentView.setCloseButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adActivityContentWrapper.shouldClose()) {
                    close();
                }
            }
        });

        View wrapperView = adActivityContentWrapper.getContentView(this);
        ViewGroup.LayoutParams lp = adActivityContentWrapper.getContentLayoutParams();
        contentView.addView(wrapperView, lp);

        setContentView(contentView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        TapItLog.d(TAG, "onPause");
        isContentStarted = false;
        setCloseButtonVisible(false);
    }

    public void setCloseButtonVisible(boolean isVisible) {
        contentView.setCloseButtonVisible(isVisible);
    }

    @Override
    public void onBackPressed() {
        // don't call super, we'll handle in close(), if allowed
//        TapItLog.d(TAG, "TapItAdActivity.onBackPressed");
        if (adActivityContentWrapper.shouldClose()) {
            close();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        TapItLog.d(TAG, "onDetachedFromWindow");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isContentStarted) {
            adActivityContentWrapper.startContent();
            isContentStarted = true;
        }
    }

    //TODO handle optional closing of this activity when user multitasks?
//    @Override
//    protected void onPause() {
//        super.onPause();
//        TapItLog.d(TAG, "onPause");
//    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void enableSystemUIAutoDimming() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // dim the lights on newer devices
            final Handler handler = new Handler(Looper.getMainLooper());
            final Runnable dimmingRunnable = new Runnable(){
                @Override
                public void run() {
                    TapItAdActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            };
            handler.post(dimmingRunnable);
            // re-dim a short time after user interacts w/ system UI
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                        handler.postDelayed(dimmingRunnable, 2000);
                    }
                }
            });
        }
    }

    /**
     * if ad interaction causes external activity to be loaded (such as google play),
     * this is called after that activity completes... (e.g. user hits back).
     *
     * automatically close this TapItAdActivity, returning control back to the
     * underlying app
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        TapItLog.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");
        close();
    }
}
