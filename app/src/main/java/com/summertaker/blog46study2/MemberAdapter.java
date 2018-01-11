package com.summertaker.blog46study2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.summertaker.blog46study2.common.BaseDataAdapter;
import com.summertaker.blog46study2.common.Config;
import com.summertaker.blog46study2.data.Member;
import com.summertaker.blog46study2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MemberAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Member> mMembers = new ArrayList<>();
    private boolean mIsCacheMode = false;

    private MemberInterface mMemberInterface;

    public MemberAdapter(Context context, ArrayList<Member> members, boolean isCacheMode, MemberInterface memberInterface) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMembers = members;
        this.mIsCacheMode = isCacheMode;

        this.mMemberInterface = memberInterface;
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
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.member_item, null);

            holder = new ViewHolder();
            holder.loLoading = (RelativeLayout) view.findViewById(R.id.loLoading);
            holder.cbLike = (CheckBox) view.findViewById(R.id.cbLike);
            holder.ivThumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
            holder.tvName = (TextView) view.findViewById(R.id.tvName);
            holder.tvNameSelected = (TextView) view.findViewById(R.id.tvNameSelected);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String imageUrl = member.getPictureUrl(); // member.getThumbnail();

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            holder.ivThumbnail.setImageResource(R.drawable.placeholder);
        } else {
            String fileName = Util.getUrlToFileName(imageUrl);
            File file = new File(Config.DATA_PATH, fileName);

            if (mIsCacheMode && file.exists()) {
                holder.loLoading.setVisibility(View.GONE);
                Picasso.with(mContext).load(file).into(holder.ivThumbnail);
                //Log.d(mTag, fileName + " local loaded.");
            } else {
                final RelativeLayout loLoading = holder.loLoading;

                Picasso.with(mContext).load(imageUrl).into(holder.ivThumbnail, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        loLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        loLoading.setVisibility(View.GONE);
                        Log.e(mTag, "Picasso Image Load Error...");
                    }
                });

                Picasso.with(mContext).load(imageUrl).into(getTarget(fileName));
            }
        }

        holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member m = mMembers.get(position);
                mMemberInterface.onPicutreClick(m);
            }
        });

        holder.cbLike.setChecked(member.isOshimember());
        holder.cbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member m = mMembers.get(position);
                mMemberInterface.onLikeClick((CheckBox) view, m);
            }
        });

        if (member.isOshimember()) {
            holder.tvName.setVisibility(View.GONE);
            holder.tvNameSelected.setText(member.getName());
            holder.tvNameSelected.setVisibility(View.VISIBLE);
        } else {
            holder.tvName.setText(member.getName());
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvNameSelected.setVisibility(View.GONE);
        }

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member m = mMembers.get(position);
                mMemberInterface.onNameClick(m);
            }
        });

        holder.tvNameSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member m = mMembers.get(position);
                mMemberInterface.onNameClick(m);
            }
        });

        return view;
    }

    //target to save
    private Target getTarget(final String fileName) {
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean isSuccess;

                        File file = new File(Config.DATA_PATH, fileName);
                        if (file.exists()) {
                            isSuccess = file.delete();
                            //Log.d("==", fileName + " deleted.");
                        }
                        try {
                            isSuccess = file.createNewFile();
                            if (isSuccess) {
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.flush();
                                ostream.close();
                                //Log.d("==", fileName + " created.");
                            } else {
                                Log.e("==", fileName + " FAILED.");
                            }
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(mTag, "IMAGE SAVE ERROR!!! onBitmapFailed()");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        CheckBox cbLike;
        ImageView ivThumbnail;
        TextView tvName;
        TextView tvNameSelected;
    }
}
