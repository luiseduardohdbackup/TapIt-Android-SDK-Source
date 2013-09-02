package com.yourcompany.player;

import com.phunware.tapitvastsdk.player.TVASTPlayer;
import com.phunware.tapitvastsdk.player.TVASTTrackingVideoView;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

/**
 * An example video player that implements VideoAdPlayer. 
 */
public class VideoDemoPlayer extends FrameLayout implements TVASTPlayer, View.OnClickListener {
  private TVASTTrackingVideoView contentVideo;
  
  private String savedContentUrl;
  private Uri savedContentUri;
  private int savedContentPosition;
  
  public VideoDemoPlayer(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public VideoDemoPlayer(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public VideoDemoPlayer(Context context) {
    super(context);
    init();
  }
  
  private void init() {
	savedContentUri = null;
	savedContentUrl = null;
	savedContentPosition = 0;
    contentVideo = new TVASTTrackingVideoView(getContext(), this);
    addView(contentVideo, 0);
    
    super.setOnClickListener(this);
  }
  
  public VideoView getVideoView() {
	return contentVideo;
  }
  
  public void playContent(String contentUrl) {
	savedContentPosition = 0;	
    savedContentUri = null;
	savedContentUrl = contentUrl;
    contentVideo.setVideoPath(contentUrl);
    contentVideo.start();
  }
  
  public void playContent(Uri contentUri) {
	savedContentPosition = 0;	
	savedContentUrl = null;
    savedContentUri = contentUri;
    contentVideo.setVideoURI(contentUri);
    contentVideo.start();
  }
	  
  public void pauseContent() {
    savedContentPosition = (contentVideo.getCurrentPosition() > 0)?contentVideo.getCurrentPosition():savedContentPosition;
    contentVideo.pause();
  }

  public void resumeContent() {
	if (savedContentUri != null)
	  contentVideo.setVideoURI(savedContentUri);
	else
      contentVideo.setVideoPath(savedContentUrl);
    contentVideo.seekTo(savedContentPosition);
    //contentVideo.resume();
    contentVideo.start();
  }

  // this is from a button click (useless!) calls from the MainActivity.
  @Override
  public void onClick(View view) {
	contentVideo.onClick();
  }
  
  @Override
  public void playAd(String url) {
	contentVideo.setVideoPath(url);
	contentVideo.start();
  }
  
  @Override
  public void stopAd() {
	contentVideo.stopPlayback();
  }
  
  @Override
  public void addCallback(TVASTAdPlayerListener callback) {
	contentVideo.addCallback(callback);
  }

  @Override
  public void removeCallback(TVASTAdPlayerListener callback) {
	  contentVideo.removeCallback(callback);
  }
}
