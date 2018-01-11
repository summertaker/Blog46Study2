package com.summertaker.blog46study2.common;

import android.os.Environment;

public class Config {

    public final static String PACKAGE_NAME = "com.summertaker.blog46study2";
    public final static String USER_PREFERENCE_KEY = PACKAGE_NAME;

    public static String USER_AGENT_DESKTOP = "Mozilla/5.0 (Macintosh; U; Mac OS X 10_6_1; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/ Safari/530.5";

    public static String USER_AGENT_MOBILE = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";

    public static String DATA_PATH = Environment.getExternalStorageDirectory().toString() + java.io.File.separator + "android" + java.io.File.separator + "data" + java.io.File.separator + PACKAGE_NAME;

    public static final String PREFERENCE_KEY_OSHIMEMBERS = "oshimembers";

    public static final int REQUEST_CODE = 100;

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static int CACHE_EXPIRE_TIME = 60; // 60분
}
