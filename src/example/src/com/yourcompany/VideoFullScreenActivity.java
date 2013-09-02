package com.yourcompany;

import com.phunware.tapitvastsdk.TVASTVideoAdsManager;
import com.phunware.tapitvastsdk.TVASTAdView;
import com.phunware.tapitvastsdk.player.TVASTPlayer.TVASTAdPlayerListener;
import com.phunware.tapitvastsdk.player.TVASTPlayer;
import com.phunware.tapitvastsdk.player.TVASTSharable;
import com.yourcompany.player.VideoDemoPlayer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

public class VideoFullScreenActivity extends Activity implements TVASTAdPlayerListener {

    public static final String EXTRA_AD_CLICKED  = "EXTRA_AD_CLICKED";
    public static final String EXTRA_AD_PLAYED  = "EXTRA_AD_PLAYED";
    public static final String EXTRA_AD_MANAGER = "EXTRA_AD_MANAGER";
	
    private TVASTVideoAdsManager mAdsManager;
    private boolean mAdPlayed;
    private boolean mAdClicked;
    private VideoDemoPlayer mVideoPlayer;
    private TVASTAdView mWebView;
    private MediaController mMediaController;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.video_fullscreen);

	    mAdClicked = false;
	    mAdPlayed = false;
	    mVideoPlayer = (VideoDemoPlayer)findViewById(R.id.adPlayer);
	    mWebView = (TVASTAdView)findViewById(R.id.closingFrame);
        if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    TVASTSharable sharedAdsManager = extras.getParcelable(EXTRA_AD_MANAGER);
		    if (sharedAdsManager != null) {
		    	mAdsManager = (TVASTVideoAdsManager)sharedAdsManager.obj();
		    }
		    
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            TVASTSharable sharedAdsManager = savedInstanceState.getParcelable(EXTRA_AD_MANAGER);
            if (sharedAdsManager != null) {
            	mAdsManager = (TVASTVideoAdsManager)sharedAdsManager.obj();
            }
            mAdClicked = savedInstanceState.getBoolean(EXTRA_AD_CLICKED);
            mAdPlayed = savedInstanceState.getBoolean(EXTRA_AD_PLAYED);
            if (!mAdPlayed) {
	            
	    	    mMediaController = new MediaController(this);
	    	    mMediaController.setAnchorView(mVideoPlayer);
	    	    //mVideoPlayer.getVideoView().setMediaController(mMediaController);
	
	    	    OnPreparedListener preparedListener = new OnPreparedListener() {
	    	        @Override
	    	        public void onPrepared(final MediaPlayer mp) {
	    	        	mAdsManager.onVideoPlay(mVideoPlayer);
	    	        }
	    	    };
	    	    mVideoPlayer.getVideoView().setOnPreparedListener(preparedListener);
	    	    mVideoPlayer.addCallback(this);
	    	    
	    		mAdsManager.play(mVideoPlayer);
            }
            else {
				mWebView.setVisibility(View.VISIBLE);
				if (!mAdClicked)
					mAdsManager.showClosingFrame(mWebView);
				else {
					mAdsManager.setAdView(mWebView);
					mAdsManager.onVideoClick(mVideoPlayer);
				}
            }
        }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
	        TVASTSharable sharedAdsManager = new TVASTSharable(mAdsManager);
			outState.putParcelable(EXTRA_AD_MANAGER, sharedAdsManager);
            outState.putBoolean(EXTRA_AD_PLAYED, mAdPlayed);
            outState.putBoolean(EXTRA_AD_CLICKED, mAdClicked);
		}
		catch (Exception e) {
			Log.d("TapItVASTSDKTest", "Exception encountered in FullScreenVideoActivity.onSaveInstanceState: "+e.getMessage());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAdsManager != null && mAdPlayed)
			mAdsManager.unload();
		mVideoPlayer.removeCallback(this);
	}
	
	@Override
	public void onVideoClick(TVASTPlayer player) {
		//Log.d("TapItVASTSDKTest","onVideoClickFS");
		mAdClicked = true;
		mAdPlayed = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//mAdsManager.loadDestinationUrl(mWebView);
	}

	@Override
	public void onVideoComplete(TVASTPlayer player) {
		Log.d("TapItVASTSDKTest","onVideoCompleteFS");

		mAdPlayed = true;
		
        // request portrait mode for showing interstitial closing frame.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // will cause the device to rotate and activity will be recreated.
	}

	@Override
	public void onVideoError(TVASTPlayer player) {
		//Log.d("TapItVASTSDKTest","onVideoErrorFS");
	}
	
	@Override
	public void onVideoPause(TVASTPlayer player) {
		//Log.d("TapItVASTSDKTest","onVideoPauseFS");
	}

	@Override
	public void onVideoPlay(TVASTPlayer player) {
		//Log.d("TapItVASTSDKTest","onVideoPlayFS");
	}
	
	@Override
	public void onVideoProgress(TVASTPlayer player, int current, int max) {
		//Log.d("TapItVASTSDKTest","onVideoProgressFS");
	}

	@Override
	public void onVideoResume(TVASTPlayer player) {
		//Log.d("TapItVASTSDKTest","onVideoResumeFS");
	}

	@Override
	public void onVideoVolumeChanged(TVASTPlayer player, int volume) {
		//Log.d("TapItVASTSDKTest","onVideoVolumeChangedFS");
	}
}
