package com.summertaker.blog46study2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.summertaker.blog46study2.common.BaseActivity;
import com.summertaker.blog46study2.common.BaseApplication;
import com.summertaker.blog46study2.common.Config;
import com.summertaker.blog46study2.data.Group;

import java.util.ArrayList;

public class GroupActivity extends BaseActivity {

    private Group mGroup;

    private ArrayList<Group> mGroups = new ArrayList<>();

    private boolean mIsDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);

        mContext = GroupActivity.this;

        //Intent intent = getIntent();
        //mGroupId = intent.getStringExtra("groupId");

        mGroups = BaseApplication.getInstance().getGroups();

        initToolbar(null);
        initToolbarProgressBar();

        initGridView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initGridView() {
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(new GroupAdapter(mContext, mGroups));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGroup = (Group) parent.getItemAtPosition(position);
                //showToolbarProgressBar();
                //loadGroup();

                Intent intent = new Intent(mContext, MemberActivity.class);
                intent.putExtra("groupId", mGroup.getId());
                startActivityForResult(intent, Config.REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.REQUEST_CODE && data != null) { // && resultCode == Activity.RESULT_OK) {
            //Log.e(mTag, ">> onActivityResult()...");

            //Intent getIntent = getIntent();
            mIsDataChanged = data.getBooleanExtra("isDataChanged", false);
            //Log.e(mTag, "mIsDataChanged: " + mIsDataChanged);

            setResult(RESULT_OK, getIntent().putExtra("isDataChanged", mIsDataChanged));
        }
    }
}
