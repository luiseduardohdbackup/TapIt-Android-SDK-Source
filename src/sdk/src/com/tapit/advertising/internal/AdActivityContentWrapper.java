package com.tapit.advertising.internal;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Used to pass necessary data to {@link TapItAdActivity}
 */
public abstract class AdActivityContentWrapper {

    /**
     * TapItAdActivity calls this to get the view to show as it's content view
     * @return the view to be used as TapItAdActivity's content view.
     */
    public abstract View getContentView(TapItAdActivity activity);

    /**
     * By default, LP's are set to wrap content, with center gravity.
     * @return the layout params to use when adding the content view.
     */
    public ViewGroup.LayoutParams getContentLayoutParams() {
        return new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    Gravity.CENTER);
    }

    /**
     * Called when activity finished and control has returned to the app
     */
    public abstract void done();

    /**
     * Called when TapItAdActivity is displayed
     */
    public void startContent() {

    }

    /**
     * Called as TapItAdActivity is being torn down
     */
    public void stopContent() {

    }

    /**
     * Called when activity is attempting to close, usually by close or back button
     * @return true if Activity can close, false to block closing
     */
    public boolean shouldClose() {
        return true;
    }
}
