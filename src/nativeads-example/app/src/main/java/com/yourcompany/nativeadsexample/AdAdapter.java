package com.yourcompany.nativeadsexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.List;

public class AdAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Ad> mListOfAds;
    private final LayoutInflater mInflater;
    private AQuery mAQ;


    public AdAdapter(Context context, List<Ad> listOfAds) {

        mContext = context;
        mListOfAds = listOfAds;
        mInflater = LayoutInflater.from(context);

        mAQ = new AQuery(mContext);
    }

    @Override
    public int getCount() {
        return mListOfAds.size();
    }

    @Override
    public Object getItem(int i) {
        return mListOfAds.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            View root = mInflater.inflate(R.layout.nativeadunit, viewGroup, false);
            holder.imageIcon = (ImageView) root.findViewById(R.id.imageViewAdIcon);
            holder.textTitle = (TextView) root.findViewById(R.id.textViewAdTitle);
            holder.ratingBarStars = (RatingBar) root.findViewById(R.id.ratingBarStars);
            holder.ratingBarStars.setEnabled(false);
            holder.webViewHtml = (WebView) root.findViewById(R.id.webViewHtml);
            holder.webViewHtml.getSettings().setLoadWithOverviewMode(true);
            holder.webViewHtml.getSettings().setUseWideViewPort(true);
            holder.textText = (TextView) root.findViewById(R.id.textViewAdText);
            holder.buttonCta = (Button) root.findViewById(R.id.buttonCta);
            root.setTag(holder);
            view = root;
        } else {
            holder = (ViewHolder) view.getTag();
        }
        bindView(i, holder);
        return view;
    }

    private void bindView(int i, ViewHolder holder) {
        final Ad tempAd = (Ad) getItem(i);
        holder.ad = tempAd;
        mAQ.id(holder.imageIcon).image(tempAd.getImageurl());
        holder.textTitle.setText(tempAd.getAdtitle());
        holder.ratingBarStars.setRating(tempAd.getStars());
        holder.textText.setText(tempAd.getAdtext());
        holder.webViewHtml.loadData(tempAd.getHtml(), "text/html", "UTF-8");
        holder.textText.setText(tempAd.getAdtext());
        holder.buttonCta.setText(tempAd.getCta());
        holder.buttonCta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempAd.click(mContext);
            }
        });
        tempAd.impression();
    }

    public static class ViewHolder {
        private ImageView imageIcon;
        private TextView textTitle;
        private RatingBar ratingBarStars;
        private WebView webViewHtml;
        private TextView textText;
        private Button buttonCta;
        public Ad ad;

    }
}
