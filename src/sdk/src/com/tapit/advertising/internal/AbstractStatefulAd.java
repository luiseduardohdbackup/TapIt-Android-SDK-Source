package com.tapit.advertising.internal;

import com.tapit.core.TapItLog;

public abstract class AbstractStatefulAd {
    protected static final String TAG = "TapIt";

    protected enum State { NEW, LOADING, LOADED, SHOWN, DONE }
    protected boolean showImmediately = false;
    protected State state = State.NEW;

    protected boolean updateState(State newState) {
        if (newState.compareTo(state) > 0) {
            synchronized(this) {
                state = newState;
            }
            return true;
        }
        TapItLog.d(TAG, "Invalid state transition: " + state + " -> " + newState);
        return false;
    }

    public abstract void doLoad();

    public void load() {
        if (updateState(State.LOADING)) {
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

    public abstract void doShow();

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
                doShow();
                updateState(State.SHOWN);

                break;

            case SHOWN:
            case DONE:
            default:
                TapItLog.w(TAG, "Ignoring attempt to re-use interstitial.");
                break;
        }

    }
}
