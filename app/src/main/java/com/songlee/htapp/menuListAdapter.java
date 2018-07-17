package com.songlee.htapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walter_tong on 3/30/17.
 */

public class menuListAdapter extends BaseAdapter  {
    private Context mContext;
    private List<Bean> mList =new ArrayList<Bean>();
    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    public menuListAdapter(Context context, List<Bean> list) {
        mContext = context;
        mList = list;
        notifyDataSetChanged();
    }


    public void refreshList(List data) {
        mList = data;
        notifyDataSetChanged();
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.textview_list_text);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageview_list_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //set text and url
        String title= mList.get(position).getText().toString();
        holder.textView.setText(title);
        Picasso.with(mContext).load(mList.get(position).getUrl()).placeholder(R.drawable.progress_animation).error(R.drawable.error).into(holder.imageView);

        return convertView;
    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;

    }

}


