package com.songlee.htapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by walter_tong on 3/30/17.
 */

public class ImageListAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;

    public ImageListAdapter(Context context, List<String> list){
        mContext=context;
        mList=list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //use converview recycle
        if(convertView==null){
            holder=new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_list_item, parent, false);
            holder.imageView= (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set text and url
        for(int i=0;i<mList.size();i++) {
            Picasso.with(mContext).load(mList.get(position)).into(holder.imageView);
        }
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;

    }
}



