package com.summertaker.blog46study2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.blog46study2.common.BaseActivity;
import com.summertaker.blog46study2.common.BaseApplication;
import com.summertaker.blog46study2.common.Config;
import com.summertaker.blog46study2.data.Group;
import com.summertaker.blog46study2.data.Member;
import com.summertaker.blog46study2.parser.Keyakizaka46Parser;
import com.summertaker.blog46study2.parser.Nogizaka46Parser;
import com.summertaker.blog46study2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberActivity extends BaseActivity implements MemberInterface {

    private Group mGroup;

    RelativeLayout mLoLoading;
    LinearLayout mLoContent;

    private ArrayList<Member> mMembers = new ArrayList<>();

    private MemberAdapter mAdapter;
    private GridView mGridView;

    private boolean mIsCacheMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_activity);

        mContext = MemberActivity.this;

        Intent intent = getIntent();
        String groupId = intent.getStringExtra("groupId");
        mGroup = BaseApplication.getInstance().getGroupById(groupId);

        String path = Config.DATA_PATH;
        File dir = new File(path);
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs(); // 이미지 파일 저장 위치 생성 (권한은 MainActivity에서 미리 획득)
        }

        initToolbar(mGroup.getName());

        mLoLoading = findViewById(R.id.loLoading);
        mGridView = findViewById(R.id.gridView);

        loadGroup();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.member, menu);
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

    @Override
    public void onStop() {
        super.onStop();

        BaseApplication.getInstance().cancelPendingRequests(mVolleyTag);

        ArrayList<Member> oshiMembers = BaseApplication.getInstance().getOshimembers();
        //Log.e(mTag, "oshiMembers.size() = " + oshiMembers.size());

        BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_OSHIMEMBERS, oshiMembers);
        BaseApplication.getInstance().setmOshimembers(oshiMembers);
    }

    private void loadGroup() {
        //Log.e(mTag, "mGroup.getUrls().size(): " + mGroup.getUrls().size());

        if (mGroup.getUrl() == null) {
            Util.alert(mContext, getString(R.string.error), "URL is null.", MemberActivity.this);
        } else {
            if (mIsCacheMode) {
                String fileName = Util.getUrlToFileName(mGroup.getUrl()) + ".html";
                //Log.e(mTag, "fileName: " + fileName);

                File file = new File(Config.DATA_PATH, fileName);
                if (file.exists()) {
                    /*
                    //------------------------------------------------
                    // 새로 고침 버튼이 있으니 날짜 비교를 하지 않는다.
                    //------------------------------------------------
                    Date lastModDate = new Date(file.lastModified());
                    //Log.e(mTag, "File last modified: " + lastModDate.toString());

                    boolean isSameDate = Util.isSameDate(lastModDate, Calendar.getInstance().getTime());
                    if (isSameDate) {
                        //Log.e(mTag, ">>>>> parseData()...");
                        parseData(Util.readFile(fileName));
                    } else {
                        //Log.e(mTag, ">>>>> requestData()...");
                        requestData(mGroup.getUrl());
                    }
                    */
                    parseData(Util.readFile(fileName));

                } else {
                    requestData(mGroup.getUrl());
                }
            } else {
                requestData(mGroup.getUrl());
            }
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
                headers.put("User-agent", mGroup.getUserAgent());
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void writeData(String url, String response) {
        Util.writeToFile(Util.getUrlToFileName(url) + ".html", response);
        parseData(response);
    }

    private void parseData(String response) {
        if (response.isEmpty()) {
            Util.alert(mContext, getString(R.string.error), "response is empty.", null);
        } else {
            if (mGroup.getId().equals("nogizaka46")) {
                Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                nogizaka46Parser.parseBlogList(response, mGroup, mMembers);
            } else if (mGroup.getId().equals("keyakizaka46")) {
                Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                keyakizaka46Parser.parseBlogList(response, mGroup, mMembers);
            }

            renderData();
        }
    }

    private void renderData() {
        mLoLoading.setVisibility(View.GONE);

        ArrayList<Member> oshiMembers = BaseApplication.getInstance().getOshimembers();
        for (Member m : mMembers) {
            for (Member o : oshiMembers) {
                if (m.getBlogUrl().equals(o.getBlogUrl())) {
                    m.setOshimember(true);
                }
            }
        }

        mAdapter = new MemberAdapter(mContext, mMembers, mIsCacheMode, this);
        mGridView.setAdapter(mAdapter);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void refresh() {
        mIsCacheMode = false;

        mMembers.clear();
        //mAdapter.notifyDataSetChanged();

        mLoLoading.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.GONE);

        loadGroup();
    }

    @Override
    public void onPicutreClick(Member member) {
        saveData(member);
    }

    @Override
    public void onLikeClick(CheckBox checkBox, Member member) {
        saveData(member);
    }

    @Override
    public void onNameClick(Member member) {
        saveData(member);
    }

    private void saveData(Member member) {
        member.setOshimember(!member.isOshimember());
        boolean isOshimember = member.isOshimember();

        ArrayList<Member> oshiMembers = BaseApplication.getInstance().getOshimembers();

        if (isOshimember) { // 추가
            boolean isExist = false;
            for (Member m : oshiMembers) {
                if (m.getBlogUrl().equals(member.getBlogUrl())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                oshiMembers.add(member);
            }
        } else { // 제거
            ArrayList<Member> members = new ArrayList<>();
            for (Member m : oshiMembers) {
                if (!m.getBlogUrl().equals(member.getBlogUrl())) {
                    members.add(m);
                }
            }
            oshiMembers = members;
        }

        BaseApplication.getInstance().setmOshimembers(oshiMembers);
        //Log.e(mTag, "mOshiMembers.size() = " + oshiMembers.size());

        mAdapter.notifyDataSetChanged();

        //---------------------------------------------------------------------
        // 이전 Activity 에 데이터 전달하기
        // onPuase(), onStop(), onDestroy() 모두에 적용시키기 위해 미리 실행
        //---------------------------------------------------------------------
        setResult(RESULT_OK, getIntent().putExtra("isDataChanged", true));
    }
}
