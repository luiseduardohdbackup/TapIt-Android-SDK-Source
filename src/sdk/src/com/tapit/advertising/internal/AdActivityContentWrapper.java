package com.tapit.advertising.internal;

import android.view.View;

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
