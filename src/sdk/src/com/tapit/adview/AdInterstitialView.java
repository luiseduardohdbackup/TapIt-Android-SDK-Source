package com.tapit.adview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Viewer of interstitial advertising.
 */
public class AdInterstitialView extends AdInterstitialBaseView {
    protected static final float CLOSE_BUTTON_SIZE_DP = 50.0f;
    protected static final float CLOSE_BUTTON_PADDING_DP = 8.0f;

    protected ImageButton closeButton;

    public AdInterstitialView(Context context, String zone){
        super(context, zone);
        buildInterstitialCloseButton();
        setAdtype("2");
        setMraidPlacementType(MraidPlacementType.INTERSTITIAL);
    }

    @Override
    public View getInterstitialView(Context ctx){
        callingActivityContext = ctx;
        interstitialLayout = new RelativeLayout(ctx);
        final RelativeLayout.LayoutParams adViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        ViewGroup parent = (ViewGroup)this.getParent();
        if (parent != null) {
            parent.removeView(this);
        }
        interstitialLayout.addView(this, adViewLayout);
        showInterstitialCloseButton();
        return interstitialLayout;
    }

    private void buildInterstitialCloseButton() {
        StateListDrawable states = new StateListDrawable();

        try {
            states.addState(new int[]{-android.R.attr.state_pressed}, getResources().getDrawable(android.R.drawable.ic_notification_clear_all));
        } catch (RuntimeException e){
            TILog.e("and error occurred", e);
        }
        closeButton = new ImageButton(context);
        closeButton.setImageDrawable(states);
        closeButton.setBackgroundDrawable(null);
        closeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                closeInterstitial();
            }
        });
    }

    protected void showInterstitialCloseButton() {
        final float scale = getResources().getDisplayMetrics().density;
        int buttonSize = (int) (CLOSE_BUTTON_SIZE_DP * scale + 0.5f);
        int buttonPadding = (int) (CLOSE_BUTTON_PADDING_DP * scale + 0.5f);
        RelativeLayout.LayoutParams buttonLayout = new RelativeLayout.LayoutParams(
                buttonSize, buttonSize);
        buttonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayout.setMargins(buttonPadding, 0, buttonPadding, 0);
        ViewGroup parent = (ViewGroup)closeButton.getParent();
        if(parent != null) {
            parent.removeView(closeButton);
        }
        interstitialLayout.addView(closeButton, buttonLayout);
    }

    public void useCustomCloseButton(boolean useCustomClose) {
        int visiblity = useCustomClose ? GONE : VISIBLE;
        closeButton.setVisibility(visiblity);
    }


    @Override
    public void click(String url){
        closeButton.setVisibility(GONE);
        super.click(url);
    }

    @Override
    public void end(AdViewCore adView){
        super.end(adView);
    }
}
