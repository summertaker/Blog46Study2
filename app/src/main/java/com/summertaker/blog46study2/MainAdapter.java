package com.summertaker.blog46study2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.summertaker.blog46study2.common.BaseDataAdapter;
import com.summertaker.blog46study2.common.Config;
import com.summertaker.blog46study2.data.Article;
import com.summertaker.blog46study2.data.Member;
import com.summertaker.blog46study2.util.ProportionalImageView;
import com.summertaker.blog46study2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Member> mMembers = new ArrayList<>();

    public MainAdapter(Context context, ArrayList<Member> members) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMembers = members;
    }

    @Override
    public int getCount() {
        return mMembers.size();
    }

    @Override
    public Object getItem(int position) {
        return mMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        final Member member = mMembers.get(position);

        if (view == null) {
            //LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.main_item, null);

            holder = new ViewHolder();
            holder.pbLoading = view.findViewById(R.id.pbLoading);
            holder.ivPicture = view.findViewById(R.id.ivPicture);
            holder.tvNew = view.findViewById(R.id.tvNew);
            holder.tvName = view.findViewById(R.id.tvName);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String imageUrl = member.getPictureUrl(); // member.getThumbnail();

        String fileName = Util.getUrlToFileName(imageUrl);
        File file = new File(Config.DATA_PATH, fileName);
        Picasso.with(mContext).load(file).into(holder.ivPicture);

        holder.tvName.setText(member.getName());

        if (member.isUpdated()) {
            holder.tvNew.setVisibility(View.VISIBLE);
        } else {
            holder.tvNew.setVisibility(View.GONE);
        }

        return view;
    }

    static class ViewHolder {
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvNew;
        TextView tvName;
    }
}

