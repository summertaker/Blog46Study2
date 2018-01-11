package com.summertaker.blog46study2;

import android.widget.CheckBox;

import com.summertaker.blog46study2.data.Member;

public interface MemberInterface {

    void onPicutreClick(Member member);

    void onLikeClick(CheckBox checkBox, Member member);

    void onNameClick(Member member);
}
