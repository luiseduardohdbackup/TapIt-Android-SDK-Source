package com.tapit.vastsdk;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.tapit.core.TapItLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TVASTLinearAd implements Parcelable {
    private static final String TAG = "TapIt";

    private String mSkipOffset;
    private String mAdParams;
    private boolean mAdParamsEncoded;
    private String mDuration;
    private int mSelectedMediaIndex;
    private List<TVASTMediaFile> mMediaFiles;
    private HashMap<String, ArrayList<String>> mTrackingEvents;
    private String mClickThrough;
    private ArrayList<String> mClickTracking;
    private ArrayList<String> mCustomClick;
    private List<TVASTLinearIcon> mIcons;

    public String getSkipOffset() {
        return mSkipOffset;
    }

    protected void setSkipOffset(String skipOffset) {
        mSkipOffset = skipOffset;
    }

    public String getAdParams() {
        return mAdParams;
    }

    protected void setAdParams(String adParams) {
        mAdParams = adParams;
    }

    public boolean getAdParamsEncoded() {
        return mAdParamsEncoded;
    }

    protected void setAdParamsEncoded(boolean adParamsEncoded) {
        mAdParamsEncoded = adParamsEncoded;
    }

    public String getDuration() {
        return mDuration;
    }

    protected void setDuration(String duration) {
        mDuration = duration;
    }

    public int getSelectedMediaIndex() {
        return mSelectedMediaIndex;
    }

    protected void setSelectedMediaIndex(int index) {
        mSelectedMediaIndex = index;
    }

    public List<TVASTMediaFile> getMediaFiles() {
        return mMediaFiles;
    }

    protected void setMediaFiles(List<TVASTMediaFile> mediaFiles) {
        mMediaFiles = mediaFiles;

        boolean mediaFileSelected = false;
        int selectedBitrate = 0;
        int selectedIndex = 0;
        for (int index = 0; index < mediaFiles.size(); index++) {
            TVASTMediaFile mediaFile = mediaFiles.get(index);
            TapItLog.d(TAG, mediaFile.getURIMediaFile());
            if (mediaFile.getMimeType().equalsIgnoreCase("video/mp4") &&
                    !mediaFile.getURIMediaFile().trim().endsWith(".m3u8") &&
                    mediaFile.getURIMediaFile().trim().startsWith("http") ){
                if(selectedBitrate < mediaFile.getBitrate() &&
                        mediaFile.getBitrate() <= 1500 && mediaFile.getWidth() <= 480){
                    selectedBitrate = mediaFile.getBitrate();
                    mediaFileSelected = true;
                    selectedIndex = index;
                } else if(mediaFile.getWidth() > 480){
                    Log.d(TAG,"This Media URL skipped because the width is greater than 480: "+mediaFile.getURIMediaFile());
                } else if (mediaFile.getBitrate() > 1500){
                    Log.d(TAG,"This Media URL skipped because the bitrate is greater than 1500: "+mediaFile.getURIMediaFile());
                }
            }

            if (mediaFileSelected)
                this.setSelectedMediaIndex(selectedIndex);
        }
    }

    public HashMap<String, ArrayList<String>> getTrackingEvents() {
        return mTrackingEvents;
    }

    protected void setTrackingEvents(HashMap<String, ArrayList<String>> trackingEvents) {
        mTrackingEvents = trackingEvents;
    }

    public String getClickThrough() {
        return mClickThrough;
    }

    protected void setClickThrough(String clickThrough) {
        mClickThrough = clickThrough;
    }

    public ArrayList<String> getClickTracking() {
        return mClickTracking;
    }

    protected void setClickTracking(ArrayList<String> clickTracking) {
        mClickTracking = clickTracking;
    }

    public ArrayList<String> getCustomClick() {
        return mCustomClick;
    }

    protected void setCustomClick(ArrayList<String> customClick) {
        mCustomClick = customClick;
    }

    public List<TVASTLinearIcon> getIcons() {
        return mIcons;
    }

    protected void setIcons(List<TVASTLinearIcon> icons) {
        mIcons = icons;
    }

    public TVASTLinearAd() {
        mSkipOffset = null;
        mAdParams = null;
        mMediaFiles = null;
        mTrackingEvents = null;
        mClickThrough = null;
        mClickTracking = null;
        mCustomClick = null;
        mIcons = null;
        mSelectedMediaIndex = -1;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mDuration == null) ? 0 : mDuration.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TVASTLinearAd other = (TVASTLinearAd) obj;
        if (mDuration == null) {
            if (other.mDuration != null)
                return false;
        } else if (!mDuration.equals(other.mDuration))
            return false;
        return true;
    }

    public static final Creator<TVASTLinearAd> CREATOR = new Creator<TVASTLinearAd>() {

        @Override
        public TVASTLinearAd[] newArray(int size) {
            return new TVASTLinearAd[size];
        }

        @Override
        public TVASTLinearAd createFromParcel(Parcel source) {
            TVASTLinearAd linearAd = new TVASTLinearAd();
            linearAd.mSkipOffset = source.readString();
            linearAd.mAdParams = source.readString();
            linearAd.mAdParamsEncoded = source.readInt() == 1;
            linearAd.mDuration = source.readString();
            linearAd.mSelectedMediaIndex = source.readInt();
            linearAd.mMediaFiles = new ArrayList<TVASTMediaFile>();
            source.readTypedList(linearAd.mMediaFiles, TVASTMediaFile.CREATOR);
            linearAd.mTrackingEvents = new HashMap<String, ArrayList<String>>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                ArrayList<String> internalList = new ArrayList<String>();
                source.readList(internalList,null);
                linearAd.mTrackingEvents.put(key, internalList);
            }
            linearAd.mClickThrough = source.readString();
            linearAd.mClickTracking = new ArrayList<String>();
            source.readArrayList(null);
            linearAd.mCustomClick = new ArrayList<String>();
            source.readArrayList(null);
            linearAd.mIcons = new ArrayList<TVASTLinearIcon>();
            source.readTypedList(linearAd.mIcons, TVASTLinearIcon.CREATOR);

            return linearAd;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSkipOffset);
        dest.writeString(mAdParams);
        dest.writeInt(mAdParamsEncoded ? 1 : 0);
        dest.writeString(mDuration);
        dest.writeInt(mSelectedMediaIndex);
        dest.writeTypedList(mMediaFiles);
        dest.writeInt(mTrackingEvents.size());
        for (String key : mTrackingEvents.keySet()) {
            dest.writeString(key);
            dest.writeList(mTrackingEvents.get(key));
        }
        dest.writeString(mClickThrough);
        dest.writeList(mClickTracking);
        dest.writeList(mCustomClick);
        dest.writeTypedList(mIcons);
    }
}
