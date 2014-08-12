package com.tapit.advertising.internal;

import android.content.Context;
import com.tapit.core.TapItLog;

/**
 * logic for tracking ad state, such as ignoring load requests to ads that are
 * currently loading, and handling automatic "load and show" functionality when
 * end user calls ad.show() before ad.load().
 *
 * A secondary responsiblity of this class is to handle the context reference,
 * and disposing of it at the appropriate time so that we avoid memory leaks.
 *
 * This class is not thread safe.
 */
public abstract class AbstractStatefulAd {
    protected static final String TAG = "TapIt";

    protected static enum State { NEW, LOADING, LOADED, SHOWN, DONE }
    protected boolean showImmediately = false;
    protected State state = State.NEW;
    private Context context;

    final void setContext(Context context) {
        this.context = context;
    }

    /**
     * don't leak context... called after an ad is shown or has errored out.
     */
    private void cleanupContext() {
        if (context != null) {
            TapItLog.v(TAG, "releasing Context reference");
            context = null;
        }
    }

    /**
     * moves to the next state, if particular state transition is allowed.  If
     * transition is not allowed, state is not changed.
     * @param newState the state to update to
     * @return true if state change was successful, false if it was blocked.
     */
    protected boolean ratchetState(State newState) {
        if (newState.compareTo(state) > 0) {
            state = newState;

            if (state.compareTo(State.SHOWN) >= 0) {
                cleanupContext();
            }
            return true;
        }
        TapItLog.d(TAG, "Invalid state transition: " + state + " -> " + newState);
        return false;
    }

    /**
     * fill this in w/ the actual ad loading behavior, such as making a request
     * to the ad server
     */
    public abstract void doLoad();

    public void load() {
        if (ratchetState(State.LOADING)) {
            doLoad();
        }
        else if (state == State.LOADING) {
            // currently loading... do nothing
            TapItLog.d(TAG, "Ignoring attempt to load interstitial... already loading!");
        }
        else {
            // already been loaded... don't reuse interstitials!
            TapItLog.w(TAG, "Ignoring attempt to re-load interstitial.");
        }
    }

    public boolean isLoaded() {
        return (state == State.LOADED);
    }

    /**
     * fill this in w/ the actual ad show behavior, such as displaying ad view,
     * or popping up an activity.
     */
    public abstract void doShow(Context context);

    public void show() {
        switch (state) {
            case NEW:
                TapItLog.v(TAG, "Loading ad asynchronously before showing");
                load();
                // fall through
            case LOADING:
                // mark to display as soon as interstitial is rdy
                showImmediately = true;
                break;

            case LOADED:
                doShow(context);
                ratchetState(State.SHOWN);
                break;

            case SHOWN:
            case DONE:
            default:
                TapItLog.w(TAG, "Ignoring attempt to re-use interstitial.");
                break;
        }

    }
}
