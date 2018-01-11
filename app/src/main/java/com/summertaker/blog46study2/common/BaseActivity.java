package com.summertaker.blog46study2.common;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.summertaker.blog46study2.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected String mTag = "== " + getClass().getSimpleName();
    protected String mVolleyTag = mTag;

    protected Context mContext;
    protected Resources mResources;
    //protected SharedPreferences mSharedPreferences;
    //protected SharedPreferences.Editor mSharedEditor;

    protected Toolbar mBaseToolbar;
    protected ProgressBar mBaseProgressBar;

    protected void initToolbar(String title) {
        mContext = BaseActivity.this;
        mResources = mContext.getResources();

        //mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, 0);
        //mSharedEditor = mSharedPreferences.edit();

        mBaseToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mBaseToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (title != null) {
                Log.e(mTag, "title: " + title);
                actionBar.setTitle(title);
            }
        }
    }

    protected void initToolbarProgressBar() {
        //int color = 0xffffffff;
        //mBaseProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        //mBaseProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    protected void onToolbarClick() {
        //Util.alert(mContext, "Toolbar");
    }

    protected void showToolbarProgressBar() {
        mBaseProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideToolbarProgressBar() {
        mBaseProgressBar.setVisibility(View.GONE);
    }

    protected void doFinish() {
        //Intent intent = new Intent();
        //intent.putExtra("pictureId", mData.getGroupId());
        //setResult(ACTIVITY_RESULT_CODE, intent);

        finish();
    }
}
