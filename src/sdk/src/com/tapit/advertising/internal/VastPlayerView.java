package com.tapit.advertising.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.tapit.core.TapItLog;
import com.tapit.vastsdk.player.TVASTPlayer;
import com.tapit.vastsdk.player.TVASTTrackingVideoView;


public class VastPlayerView extends FrameLayout implements TVASTPlayer, TVASTPlayer.TVASTAdPlayerListener {
    private static final String TAG = "TapIt";
    private static final String COUNTDOWN_PREFIX = "Advertisement ";

    private TVASTTrackingVideoView videoView = null;
    private TextView countdownTextView = null;

    public VastPlayerView(Context context) {
        super(context.getApplicationContext());
        setup();
    }

    public VastPlayerView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
        setup();
    }

    public VastPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);
        setup();
    }

    private void setup() {
        videoView = new TVASTTrackingVideoView(getContext(), this);
        videoView.addCallback(this);

        FrameLayout.LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        addView(videoView, lp);

        countdownTextView = new TextView(getContext());
        lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.LEFT);

        addView(countdownTextView, lp);
    }

    @Override
    public void addCallback(TVASTAdPlayerListener callback) {
        TapItLog.d(TAG, "VastPlayerView.addCallback");
        videoView.addCallback(callback);
    }

    @Override
    public void playAd(String url) {
//        Uri uri=Uri.parse(url);
//        videoView.setVideoURI(uri);
        videoView.setVideoPath(url);
        videoView.requestFocus();

        videoView.start();
    }

    @Override
    public void removeCallback(TVASTAdPlayerListener callback) {
        videoView.removeCallback(callback);
    }

    @Override
    public void stopAd() {
        videoView.stopPlayback();
    }

    @Override
    public VideoView getVideoView() {
        return videoView;
    }

    @Override
    public void onVideoClick(TVASTPlayer player) {
        TapItLog.d(TAG, "VastPlayerView.onVideoClick");
    }

    @Override
    public void onVideoComplete(TVASTPlayer player) {
        countdownTextView.setText(COUNTDOWN_PREFIX + "0:00");
    }

    @Override
    public void onVideoError(TVASTPlayer player) {
        // noop
    }

    @Override
    public void onVideoPause(TVASTPlayer player) {
        // noop
    }

    @Override
    public void onVideoPlay(TVASTPlayer player) {
        // noop
    }

    @Override
    public void onVideoProgress(TVASTPlayer player, int current, int max) {
        int minutes = (max - current) / 60;
        String seconds = String.format("%02d", (max - current) % 60);
        StringBuilder sb = new StringBuilder(COUNTDOWN_PREFIX.length() + 4);
        sb.append(COUNTDOWN_PREFIX)
                .append(minutes)
                .append(':')
                .append(seconds);
        countdownTextView.setText(sb.toString());
    }

    @Override
    public void onVideoResume(TVASTPlayer player) {
        // noop
    }

    @Override
    public void onVideoVolumeChanged(TVASTPlayer player, int volume) {
        // noop
    }


    /**
     * hijack touch listener so that it only applies to the video view...
     * @param listener the OnTouchListener to hijack
     */
    @Override
    public void setOnTouchListener(OnTouchListener listener) {
        videoView.setOnTouchListener(listener);
    }
}
