package com.yourcompany;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.phunware.tapitvastsdk.TVASTAdErrorEvent;
import com.phunware.tapitvastsdk.TVASTAdErrorListener;
import com.phunware.tapitvastsdk.TVASTAdView;
import com.phunware.tapitvastsdk.TVASTAdsLoader;
import com.phunware.tapitvastsdk.TVASTAdsLoader.TVASTAdsLoadedEvent;
import com.phunware.tapitvastsdk.TVASTAdsLoader.TVASTAdsLoadedListener;
import com.phunware.tapitvastsdk.TVASTVideoAdsManager.TVASTAdEvent;
import com.phunware.tapitvastsdk.TVASTVideoAdsManager.TVASTAdEventListener;
import com.phunware.tapitvastsdk.TVASTAdsRequest;
import com.phunware.tapitvastsdk.TVASTVideoAdsManager;
import com.phunware.tapitvastsdk.player.TVASTPlayer.TVASTAdPlayerListener;
import com.phunware.tapitvastsdk.player.TVASTPlayer;
import com.phunware.tapitvastsdk.player.TVASTSharable;

import com.yourcompany.player.VideoDemoPlayer;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class VideoAdActivity extends Activity implements TVASTAdErrorListener, TVASTAdsLoadedListener, TVASTAdEventListener, TVASTAdPlayerListener {
		  
  private Uri mVideoUri;
  private TVASTAdsLoader mAdsLoader;
  private TVASTVideoAdsManager mAdsManager;

  private VideoDemoPlayer mVideoPlayer;
  private VideoDemoPlayer mAdPlayer;
  private TVASTAdView mWebView;
  private ScrollView mLogScroll;
  private TextView mLog;

  private Button mRequestAdButton;
  private Button mResetAdButton;
  private CheckBox mFullscreenAdCheckBox;

  private ImageButton mPlayButton;
  private ImageButton mPauseButton;
  private SeekBar mSeekBar;
  private TextView mHeadValueText;
  private TextView mDurationText;
  
  private boolean mContentStarted = false;
  private boolean mAdPlaying = false;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.video_main);

    mVideoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.disneyplanestrailer);
    
    mVideoPlayer = (VideoDemoPlayer) findViewById(R.id.contentVideo);
    mAdPlayer = (VideoDemoPlayer) findViewById(R.id.adVideo);
    mWebView = (TVASTAdView)findViewById(R.id.closingFrame);

    mLogScroll = (ScrollView) findViewById(R.id.scroll);
    mLog = (TextView) findViewById(R.id.log);
    
    mRequestAdButton = (Button) findViewById(R.id.requestAd);
    mResetAdButton = ((Button) findViewById(R.id.resetAd));
    mResetAdButton.setEnabled(false);

    mPlayButton = (ImageButton) findViewById(R.id.play);
    mPauseButton = (ImageButton) findViewById(R.id.pause);
    mHeadValueText = (TextView) findViewById(R.id.head_value);
    mDurationText = (TextView) findViewById(R.id.duration);
    mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
    
    mFullscreenAdCheckBox = (CheckBox) findViewById(R.id.fullscreenAd);
    
    mAdsLoader = new TVASTAdsLoader(this);
    mAdsLoader.addAdErrorListener(this);
    mAdsLoader.addAdsLoadedListener(this);

    setButtonListeners();
    mVideoPlayer.addCallback(this);
    mAdPlayer.addCallback(this);
    
    mAdPlaying = false;
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    if (mContentStarted && mVideoPlayer != null) {
      mVideoPlayer.resumeContent();
    }
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    if (mContentStarted && mVideoPlayer != null) {
      mVideoPlayer.pauseContent();
    }
  }
  
  protected void playVideo() {
    mContentStarted = true;
    
    mVideoPlayer.playContent(mVideoUri);
  }

  protected void requestAd() {
    toConsole("Requesting ads");

    TVASTAdsRequest request = new TVASTAdsRequest("22219");
    request.setRequestParameter("cid", "130902");			//"113559","128681","130902"
    request.setRequestParameter("videotype", "pre-roll");
    request.setAdtype("VIDEO");
	mAdsLoader.requestAds(request);
  }
  
  @Override
  public void onAdError(TVASTAdErrorEvent event) {
    toConsole("Ads error: " + event.getError().getMessage() + "\n");
    mResetAdButton.setEnabled(true);
    mRequestAdButton.setEnabled(true);
  }

  @Override
  public void onAdsLoaded(TVASTAdsLoadedEvent event) {
    toConsole("Ads loaded!");
    mAdsManager = (TVASTVideoAdsManager) event.getManager();
    mAdsManager.addAdErrorListener(this);
    mAdsManager.addAdEventListener(this);
    mResetAdButton.setEnabled(true);
    mRequestAdButton.setEnabled(true);

    mAdsManager.setIsFullscreen(mFullscreenAdCheckBox.isChecked());
    if (mAdsManager.isFullscreen()) {
		Intent intent = new Intent(this, VideoFullScreenActivity.class);
		TVASTSharable sharedAdsManager = new TVASTSharable(mAdsManager);
		intent.putExtra(VideoFullScreenActivity.EXTRA_AD_MANAGER, sharedAdsManager);
		startActivity(intent);
    }		
    else
    	mAdsManager.play(mAdPlayer);
  }

  @Override
  public void onAdEvent(TVASTAdEvent event) {
    toConsole("Event:" + event.getEventType());

    switch (event.getEventType()) {
      case CONTENT_PAUSE_REQUESTED:
        if (mContentStarted) {
        	mVideoPlayer.pauseContent();
        	mVideoPlayer.getVideoView().setVisibility(View.GONE);
        	mAdPlayer.getVideoView().setVisibility(View.VISIBLE);
        	mAdPlayer.bringToFront();
        	mAdPlaying = true;
        }
        break;
      case CONTENT_RESUME_REQUESTED:
        if (mContentStarted) {
        	mVideoPlayer.resumeContent();
        	mVideoPlayer.getVideoView().setVisibility(View.VISIBLE);
        	mVideoPlayer.bringToFront();
        	mAdPlayer.getVideoView().setVisibility(View.GONE);
        	mAdPlaying = false;
        }
        break;
      case CLICK:
        //mAdsManager.unload();
        mAdsManager.loadDestinationUrl(mWebView);
    	break;
    }
  }
  
  private void toConsole(String message) {
    mLog.append(message + "\n");
    mLogScroll.post(new Runnable() {
      @Override
      public void run() {
        mLogScroll.fullScroll(View.FOCUS_DOWN);
      }
    });
  }

  private void setButtonListeners() {
    mPlayButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
    	if (mAdPlaying) {
    		mAdPlayer.resumeContent();
    	}
    	else {
		    if (mContentStarted && mVideoPlayer != null) {
		      mVideoPlayer.resumeContent();
	    	}
		    else {
	    	  playVideo();
		    }
    	}
        mPlayButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);
        mLog.append("Playing content video...\n");
      }
    });

    mPauseButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mAdPlaying) {
        	mAdPlayer.pauseContent();  
          }
          else {
            mVideoPlayer.pauseContent();
          }
          mPlayButton.setVisibility(View.VISIBLE);
          mPauseButton.setVisibility(View.GONE);
        }
      });
    
    mRequestAdButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
    	  mRequestAdButton.setEnabled(false);
    	  requestAd();
      }
    });

    mResetAdButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mResetAdButton.setEnabled(false);
        if (mAdsManager != null)
        	mAdsManager.unload();
        mVideoPlayer.stopAd();
        onVideoProgress(mVideoPlayer, 0, 0);
        mContentStarted = false;        
        mPlayButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.GONE);
        mResetAdButton.setEnabled(true);
      }
    });
  }
  
  
/*
 * com.phunware.TapItVASTsdk.player.TVASTPlayer.TVASTAdPlayerListener implementations
 */
  @Override
  public void onVideoClick(TVASTPlayer player) {
	Log.d("TapItVASTSDKTest", "onVideoClick");
  }
  
  @Override
  public void onVideoComplete(TVASTPlayer player) {
	  Log.d("TapItVASTSDKTest", "onVideoComplete");
      mPlayButton.setEnabled(true);
	  
	  int current = mSeekBar.getMax();
	  mSeekBar.setProgress(current);
	  mHeadValueText.setText(mDurationText.getText());
	  
	  if (player.equals(mAdPlayer)) {
	   	  mWebView.setVisibility(View.VISIBLE);
	   	  mWebView.bringToFront();
		  mAdsManager.showClosingFrame(mWebView);
	  }
	  else {
		  ((VideoDemoPlayer)player).playContent(mVideoUri);
	  }
  }
  
  @Override
  public void onVideoError(TVASTPlayer player) {
	Log.d("TapItVASTSDKTest", "onVideoError");
  }
  
  @Override
  public void onVideoPause(TVASTPlayer player) {
	Log.d("TapItVASTSDKTest", "onVideoPause");
    toConsole("Pausing video");

    if (player.equals(mAdPlayer)) {
        mPlayButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.GONE);
    }
  }

  @Override
  public void onVideoResume(TVASTPlayer player) {
	Log.d("TapItVASTSDKTest", "onVideoResume");
    toConsole("Resuming video");
    mPlayButton.setVisibility(View.GONE);
    mPauseButton.setVisibility(View.VISIBLE);
  }

  @Override
  public void onVideoPlay(TVASTPlayer player) {
	Log.d("TapItVASTSDKTest", "onVideoPlay");
	
	if (player.equals(mAdPlayer)) {
		toConsole("Showing ad");
		
        mPlayButton.setVisibility(View.GONE);
        mPauseButton.setVisibility(View.VISIBLE);		

  	  	mAdsManager.setAdView(mWebView);	
	}
	else {
		//toConsole("Playing video");
	}
  }
  
  @Override
  @SuppressLint("DefaultLocale")
  public void onVideoProgress(TVASTPlayer player, int current, int max) {	
	  mSeekBar.setMax(max);
	  mSeekBar.setProgress(current);
	  
	  int seconds = max;
	  //int hours = seconds / 3600;
	  seconds %= 3600;
	  int minutes = seconds / 60;
	  seconds %= 60;
	  
	  String maxTime = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
	  mDurationText.setText(maxTime);
	  
	  seconds = current;
	  //hours = seconds / 3600;
	  seconds %= 3600;
	  minutes = seconds / 60;
	  seconds %= 60;
	  
	  String currentPosition = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
	  mHeadValueText.setText(currentPosition);
  }
  
  @Override
  public void onVideoVolumeChanged(TVASTPlayer player, int volume) {
	  Log.d("TapItVASTSDKTest", "onVideoVolumeChanged");
  }
}
