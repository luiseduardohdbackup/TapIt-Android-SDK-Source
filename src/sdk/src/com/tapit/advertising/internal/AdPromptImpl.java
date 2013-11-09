package com.tapit.advertising.internal;

import android.content.Context;
import com.tapit.advertising.TapItAdPrompt;
import com.tapit.advertising.TapItAdRequest;
import com.tapit.adview.AdPrompt;

public final class AdPromptImpl implements TapItAdPrompt {

    private final AdPrompt legacyAdPrompt;
    private TapItAdPromptListener listener = null;


    public static TapItAdPrompt getAdPromptForZone(Context context, String zone) {
        TapItAdRequest request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        return getAdPrompt(context, request);
    }

    /**
     * Factory method used to build AdPrompt instances
     * @param context the activity instance
     * @param request the request object containing configuration details
     * @return An AdPrompt object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static TapItAdPrompt getAdPrompt(Context context, TapItAdRequest request) {
        return new AdPromptImpl(context, request);
    }


    private AdPromptImpl(Context context, TapItAdRequest adRequest) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }

        if (adRequest == null) {
            throw new NullPointerException("Ad request cannot be null");
        }

        legacyAdPrompt = new AdPrompt(context, AdRequestImpl.asImplAdRequest(adRequest));
    }


    @Override
    public TapItAdPromptListener getListener() {
        return listener;
    }

    @Override
    public void setListener(final TapItAdPromptListener adPromptListener) {
        if (adPromptListener != null) {
            legacyAdPrompt.setListener(new AdPrompt.AdPromptCallbackListener() {
                @Override
                public void adPromptLoaded(AdPrompt adPrompt) {
                    adPromptListener.adPromptDidLoad(AdPromptImpl.this);
                }

                @Override
                public void adPromptDisplayed(AdPrompt adPrompt) {
                    adPromptListener.adPromptDisplayed(AdPromptImpl.this);
                }

                @Override
                public void adPromptError(AdPrompt adPrompt, String error) {
                    adPromptListener.adPromptDidFail(AdPromptImpl.this, error);
                }

                @Override
                public void adPromptClosed(AdPrompt adPrompt, boolean didAccept) {
                    adPromptListener.adPromptClosed(AdPromptImpl.this, didAccept);
                }
            });
        }
        else {
            legacyAdPrompt.setListener(null);
        }
        this.listener = adPromptListener;
    }

    @Override
    public final boolean isLoaded() {
        return legacyAdPrompt.isLoaded();
    }

    @Override
    public final void load() {
        legacyAdPrompt.load();
    }

    @Override
    public final void show() {
        legacyAdPrompt.showAdPrompt();
    }
}
