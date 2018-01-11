package com.summertaker.blog46study2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.summertaker.blog46study2.common.BaseActivity;
import com.summertaker.blog46study2.common.Config;
import com.summertaker.blog46study2.util.Util;

import java.io.File;
import java.io.IOException;

public class CacheActivity extends BaseActivity {

    private TextView mTvCacheSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cache_activity);

        initToolbar(null);

        /*
        String path = Config.DATA_PATH; //Environment.getExternalStorageDirectory().toString()+"/Pictures";
        Log.e("Files", "Path: " + path);

        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.e("Files", "Size: "+ files.length);

        for (int i = 0; i < files.length; i++) {
            Log.e(mTag, "FileName:" + files[i].getName());
        }
        */

        mTvCacheSize = findViewById(R.id.tvCacheSize);

        showCache();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cache, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            deleteCache();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCache() {
        long size = Util.getDirectorySize(new File(Config.DATA_PATH));
        size = size / (1024 * 1024);
        //Log.e(mTag, "size:" + size); // mega bytes

        String sizeText = size + "MB";

        mTvCacheSize.setText(sizeText);
    }

    private void deleteCache() {
        try {
            Util.deleteFiles(new File(Config.DATA_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        showCache();
    }
}
