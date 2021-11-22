package com.swiftdating.app.common;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;
import java.util.Vector;

import com.swiftdating.app.R;
import com.swiftdating.app.model.responsemodel.InstagramImageModel;

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Vector<View> pages;
    InstaAdapter instaAdapter;
    List<InstagramImageModel.Datum> imageList;
    private LayoutInflater mLayoutInflater;

    public CustomPagerAdapter(Context context, Vector<View> pages, List<InstagramImageModel.Datum> imageList) {
        this.mContext = context;
        this.pages = pages;
        this.imageList = imageList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("page",position+"");
        View itemView = mLayoutInflater.inflate(R.layout.insta_photo_grid_view, container, false);
        RecyclerView rvInsta = itemView.findViewById(R.id.rvInstaPics);
        instaAdapter = new InstaAdapter(mContext, imageList,position);
        rvInsta.setAdapter(instaAdapter);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}