package com.summertaker.blog46study2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.summertaker.blog46study2.common.BaseDataAdapter;
import com.summertaker.blog46study2.data.Group;

import java.util.ArrayList;

public class GroupAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<Group> mGroups = new ArrayList<>();

    public GroupAdapter(Context context, ArrayList<Group> groups) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroups = groups;
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        Group group = mGroups.get(position);

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.group_item, null);

            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.ivPicture);
            holder.name = (TextView) view.findViewById(R.id.tvName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.image.setImageResource(group.getLogo());
        holder.name.setText(group.getName());

        //Log.e(mTag, "item.getName(): " + item.getName());

        return view;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
    }
}
