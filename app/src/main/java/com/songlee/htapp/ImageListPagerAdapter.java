package com.songlee.htapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walter_tong on 3/30/17.
 */


    public class ImageListPagerAdapter extends PagerAdapter {

        Context context;
        LayoutInflater layoutInflater;

        List<String> arrayList;

        public ImageListPagerAdapter(Context context, List<String> arrayList) {
            this.context = context;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            if(arrayList != null){
                return arrayList.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = layoutInflater.inflate(R.layout.image_detail, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image);

            Picasso.with(context).load(arrayList.get(position))
                    .placeholder(R.drawable.abc_spinner_mtrl_am_alpha)
                    .error(R.drawable.abc_ab_share_pack_mtrl_alpha).into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }


    }