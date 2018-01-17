package com.summertaker.blog46study2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.blog46study2.common.BaseActivity;
import com.summertaker.blog46study2.common.BaseApplication;
import com.summertaker.blog46study2.common.Config;
import com.summertaker.blog46study2.data.Article;
import com.summertaker.blog46study2.data.Member;
import com.summertaker.blog46study2.parser.Keyakizaka46Parser;
import com.summertaker.blog46study2.parser.Nogizaka46Parser;
import com.summertaker.blog46study2.util.Util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private boolean mIsPermissionGranted = false;

    private int mNavItemId = 0;

    private final String mKeyLastCheckDate = "lastCheckDate";
    private String mLastCheckDateString = "";
    private Date mLastCheckDate = null;
    DateFormat mDateFormat;

    private LinearLayout mLoLoading;
    private TextView tvLoadingCount;
    private ProgressBar mPbLoadingHorizontal;

    private int mLoadCount = 0;

    private ArrayList<Member> mOshiMembers;
    private SwipeRefreshLayout mSwipeRefresh;
    private MainAdapter mAdapter;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = MainActivity.this;

        mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToolbarClick();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                if (mNavItemId == R.id.nav_member_settings) {
                    Intent intent = new Intent(mContext, GroupActivity.class);
                    startActivityForResult(intent, Config.REQUEST_CODE);
                } else if (mNavItemId == R.id.nav_cache_settings) {
                    Intent intent = new Intent(MainActivity.this, CacheActivity.class);
                    startActivity(intent);
                }

                mNavItemId = 0;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLoLoading = findViewById(R.id.loLoading);
        tvLoadingCount = findViewById(R.id.tvLoadingCount);
        mPbLoadingHorizontal = findViewById(R.id.pbLoadingHorizontal);

        mSwipeRefresh = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(this);

        mGridView = findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Member member = (Member) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(mContext, ArticleListActivity.class);
                intent.putExtra("name", member.getName());
                intent.putExtra("blogUrl", member.getBlogUrl());
                startActivity(intent);
            }
        });

        //----------------------------------------------------------------------------
        // 런타임에 권한 요청
        // https://developer.android.com/training/permissions/requesting.html?hl=ko
        //----------------------------------------------------------------------------
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Log.e(mTag, ">> onRequestPermissionsResult()...");

        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mIsPermissionGranted = true;
                    init();
                } else {
                    onPermissionDenied();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void onPermissionDenied() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(R.string.access_denied);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        //Log.e(mTag, ">> onResume()...");
        super.onResume();

        if (mIsPermissionGranted) {
            init();
        }
    }

    private void init() {
        //Log.e(mTag, ">> init()...");

        if (mAdapter == null) {
            //Log.e(mTag, ">> mAdapter is null.");

            SharedPreferences mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
            String lastCheckDate = mSharedPreferences.getString(mKeyLastCheckDate, "");
            //Log.e(mTag, "mLastCheckDate: " + mLastCheckDate);

            try {
                mLastCheckDate = mDateFormat.parse(lastCheckDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(mTag, "DATE ERROR: " + e.getMessage());
            }

            mOshiMembers = new ArrayList<>();
            mOshiMembers = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_OSHIMEMBERS);
            //Log.e(mTag, "mOshiMembers.size() = " + mOshiMembers.size());

            //for (Member member : mOshiMembers) {
            //    member.setLoading(false);
            //}

            mAdapter = new MainAdapter(mContext, mOshiMembers);
            mGridView.setAdapter(mAdapter);

            mPbLoadingHorizontal.setProgress(0);

            loadData();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //int id = item.getItemId();

        //if (id == R.id.nav_cache) {
        //    Intent intent = new Intent(MainActivity.this, CacheActivity.class);
        //    startActivityForResult(intent, 900);
        //}

        mNavItemId = item.getItemId();

        //item.setChecked(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void loadData() {
        //if (mLoadCount < mOshiMembers.size()) {
        for (Member member : mOshiMembers) {
            //Member member = mOshiMembers.get(mLoadCount);

            //checkData(member.getBlogUrl());
            requestData(member.getBlogUrl());
        }
    }

    private void checkData(String url) {
        String fileName = Util.getUrlToFileName(url) + ".html";
        //Log.e(mTag, "fileName: " + fileName);

        File file = new File(Config.DATA_PATH, fileName);
        if (file.exists()) {
            Date lastModDate = new Date(file.lastModified());
            //Log.e(mTag, "File last modified: " + lastModDate.toString());

            boolean isSameDate = Util.isSameDate(lastModDate, Calendar.getInstance().getTime());
            if (isSameDate) {
                //Log.e(mTag, ">>>>> parseData()...");
                parseData(url, Util.readFile(fileName));
            } else {
                //Log.e(mTag, ">>>>> requestData()...");
                requestData(url);
            }
        } else {
            //Log.e(mTag, ">>>>> requestData()...");
            requestData(url);
        }
    }

    private void requestData(final String url) {
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                writeData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.alert(mContext, getString(R.string.error), error.getMessage(), null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", Config.USER_AGENT_DESKTOP);
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void writeData(String url, String response) {
        Util.writeToFile(Util.getUrlToFileName(url) + ".html", response);
        parseData(url, response);
    }

    private void parseData(String url, String response) {
        if (!response.isEmpty()) {
            ArrayList<Article> articles = new ArrayList<>();
            if (url.contains("nogizaka46")) {
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                nogizaka46Parser.parseBlogDetail(response, articles);
            } else if (url.contains("keyakizaka46")) {
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                keyakizaka46Parser.parseBlogDetail(response, articles);
            }

            Member member = null;
            for (Member m : mOshiMembers) {
                if (url.equals(m.getBlogUrl())) {
                    member = m;
                    break;
                }
            }

            if (member != null) {
                if (articles.size() > 0) {
                    Article article = articles.get(0);
                    Date date = Util.getDate(article.getDate());
                    //Log.e(mTag, member.getName() + " " + date.toString());

                    if (url.equals(member.getBlogUrl())) {
                        // https://stackoverflow.com/questions/22039991/how-to-compare-two-dates-along-with-time-in-java
                        int compareTo = date.compareTo(mLastCheckDate);
                        if (compareTo > 0) {
                            member.setUpdated(true);
                        } else if (compareTo < 0) {
                            member.setUpdated(false);
                        }
                    }
                } else {
                    member.setUpdated(false);
                }
            }

            mLoadCount++;

            if (mLoadCount < mOshiMembers.size()) {
                //loadData();
                updateProgress();
            } else {
                //Collections.sort(mArticles, Collections.reverseOrder());
                renderData();
            }
        }
    }

    public void renderData() {
        String today = Util.getToday(Config.DATE_TIME_FORMAT);
        SharedPreferences mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor mSharedEditor = mSharedPreferences.edit();
        mSharedEditor.putString(mKeyLastCheckDate, today);
        mSharedEditor.apply();

        mLoLoading.setVisibility(View.GONE);
        mSwipeRefresh.setVisibility(View.VISIBLE);

        mLoadCount = 0;

        //mAdapter.notifyDataSetChanged();

        mSwipeRefresh.setRefreshing(false);

        //mCallback.onCallback("fragmentLoaded");
    }

    private void updateProgress() {
        //if (mCheckCount == 0) {
        //    LinearLayout loLoadingHorizontal = (LinearLayout) findViewById(R.id.loLoadingHorizontal);
        //    loLoadingHorizontal.setVisibility(View.VISIBLE);
        //}

        //tvLoadingName.setText(name);

        int count = mLoadCount + 1;

        String text = "( " + count + " / " + mOshiMembers.size() + " )";
        tvLoadingCount.setText(text);

        float progress = (float) count / (float) mOshiMembers.size();
        int progressValue = (int) (progress * 100.0);
        //Log.e(mTag, mUrlCount + " / " + mUrlTotal + " = " + progressValue);

        mPbLoadingHorizontal.setProgress(progressValue);
    }

    public void onToolbarClick() {
        goTop();
    }

    public void goTop() {
        //Log.e(mTag, "goTop()..." + mPosition);

        //mListView.setSelection(0);
        //mListView.smoothScrollToPosition(0);
        //mListView.setSelectionAfterHeaderView();
    }

    public void refresh() {
        //Log.e(mTag, "refresh()..." + mPosition);

        //mAdapter = null;
        mLoadCount = 0;

        tvLoadingCount.setText("");
        mPbLoadingHorizontal.setProgress(0);

        //for (Member member : mOshiMembers) {
        //    member.setLoading(true);
        //}
        //mAdapter.notifyDataSetChanged();

        //mSwipeRefresh.setRefreshing(false);

        mLoLoading.setVisibility(View.VISIBLE);
        mSwipeRefresh.setVisibility(View.GONE);

        loadData();
    }

    @Override
    public void onRefresh() {
        //mSwipeRefresh.setRefreshing(false);
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.e(mTag, ">>>>> onActivityResult()...");

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.REQUEST_CODE && data != null) { // && resultCode == Activity.RESULT_OK) {
            boolean isDataChanged = data.getBooleanExtra("isDataChanged", false);

            if (isDataChanged) {
                //refresh();
                mAdapter = null;
            }
        }
    }
}
