package com.summertaker.blog46study2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.summertaker.blog46study2.common.BaseActivity;
import com.summertaker.blog46study2.util.ImageUtil;
import com.summertaker.blog46study2.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ArticleDetailActivity extends BaseActivity {

    private ScrollView mSvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);

        mContext = ArticleDetailActivity.this;

        Intent intent = getIntent();
        String blogTitle = intent.getStringExtra("title");
        String blogName = intent.getStringExtra("name");
        String blogDate = intent.getStringExtra("date");
        String blogHtml = intent.getStringExtra("html");

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle(name);
        //setSupportActionBar(toolbar);

        initToolbar(blogName);

        mSvContent = findViewById(R.id.svContent);

        mBaseToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSvContent.scrollTo(0, 0);
                //mSvContent.smoothScrollTo(0, 0);
            }
        });

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(blogTitle);

        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        try {
            date = sdf.parse(blogDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        blogDate = Util.parseDate(blogDate);

        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText(blogDate);

        TextView tvToday = findViewById(R.id.tvToday);
        Date today = new Date();
        if (Util.isSameDate(today, date)) {
            tvToday.setVisibility(View.VISIBLE);
        }

        TextView tvYesterday = findViewById(R.id.tvYesterday);
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, -1);
        Date yesterday = c.getTime();
        if (Util.isSameDate(yesterday, date)) {
            tvYesterday.setVisibility(View.VISIBLE);
        }

        TextView tvContent = findViewById(R.id.tvContent);
        // https://medium.com/@rajeefmk/android-textview-and-image-loading-from-url-part-1-a7457846abb6
        Spannable spannable = ImageUtil.getSpannableHtmlWithImageGetter(mContext, tvContent, blogHtml);
        //ImageUtil.setClickListenerOnHtmlImageGetter(html, new ImageUtil.Callback() {
        //    @Override
        //    public void onImageClick(String imageUrl) {
        //        //Log.e(mTag, "imageUrl: " + imageUrl);
        //        //viewImage(imageUrl);
        //    }
        //}, true);
        tvContent.setText(spannable);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance()); // URL 클릭 시 이동
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
