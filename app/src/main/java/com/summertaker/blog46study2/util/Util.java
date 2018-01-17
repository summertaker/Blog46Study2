package com.summertaker.blog46study2.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.summertaker.blog46study2.R;
import com.summertaker.blog46study2.common.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    private static String mTag = "== Util";

    public static void alert(final Context context, String title, String message, final Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setMessage(message);
        if (activity == null) {
            alert.setCancelable(true);
        } else {
            alert.setPositiveButton(context.getString(R.string.finish), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.finish();
                }
            });
        }
        alert.setNegativeButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public static String getUrlToFileName(String url) {
        String fileName = url;

        fileName = fileName.replace("http://", "");
        fileName = fileName.replace("https://", "");
        fileName = fileName.replace("/", "");
        fileName = fileName.replace(".", "");
        fileName = fileName.replace("?", "");
        fileName = fileName.replace("&", "");
        fileName = fileName.replace("=", "");
        fileName = fileName.replace("_", "");
        //Log.d(mTag, fileName);

        return fileName;
    }

    public static String getJapaneseString(String text, String encoding) {
        if (encoding == null) {
            encoding = "ISO-8859-1"; // // JIS, SJIS, 8859_1, SHIFT-JIS
        }
        try {
            return new String(text.getBytes(encoding), Charset.forName("UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }


    public static String readFile(String fileName) {

        String result = "";

        File file = new File(Config.DATA_PATH, fileName);
        if (file.exists()) {
            StringBuilder builder = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    //builder.append('\n');
                }
                reader.close();
            } catch (IOException e) {
                Log.e(mTag, "FILE: " + fileName);
                Log.e(mTag, "ERROR: " + e.getLocalizedMessage());
            }
            //Log.d(mTag, builder.toString());
            result = builder.toString();
        }

        return result;
    }

    public static void writeToFile(String fileName, String data) {
        File file = new File(Config.DATA_PATH, fileName);

        try {
            boolean isSuccess = true;

            if (file.exists()) {
                isSuccess = file.delete();
                if (isSuccess) {
                    file = new File(Config.DATA_PATH, fileName);
                }
            }
            if (isSuccess) {
                isSuccess = file.createNewFile();
                //Log.e(mTag, "writeToFile().isSuccess: " + isSuccess);

                if (isSuccess) {
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                    myOutWriter.append(data);
                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();
                }
            }
        } catch (IOException e) {
            Log.e(mTag, "FILE: " + fileName);
            Log.e(mTag, "ERROR: " + e.getLocalizedMessage());
        }
    }

    public static String getToday(String format) {
        // 2016년 3월 22일 (금)
        //Date now = new Date();
        //DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        //String today = df.format(now);

        // 2016년 3월 22일 화요일 오후 10:45
        //String dateTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(now);

        Calendar calendar = Calendar.getInstance();
        if (format == null) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static boolean isSameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null...");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDate(cal1, cal2);
    }

    public static boolean isSameDate(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static String parseDate(String str) {
        String result = "";
        Date date;
        try {
            DateFormat sdf = null;
            if (str.contains("+")) {
                sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                date = sdf.parse(str);
                result = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
            } else if (str.contains("/")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                date = sdf.parse(str);
                result = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date);
            } else if (str.contains("-")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd E", Locale.ENGLISH);
                date = sdf.parse(str);
                result = DateFormat.getDateInstance(DateFormat.FULL).format(date);
            } else if (str.contains(".") && str.length() <= 10) {
                sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
                date = sdf.parse(str);
                result = DateFormat.getDateInstance(DateFormat.FULL).format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Date getDate(String str) {
        Date date = null;
        try {
            DateFormat sdf = null;
            if (str.contains("+")) {
                sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                date = sdf.parse(str);
            } else if (str.contains("/")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
                date = sdf.parse(str);
            } else if (str.contains("-")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd E", Locale.ENGLISH);
                date = sdf.parse(str);
            } else if (str.contains(".") && str.length() <= 10) {
                sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
                date = sdf.parse(str);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static long getDirectorySize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += getDirectorySize(file);
        }
        return length; // bytes
    }

    public static void deleteFiles(File directory) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isFile())
                file.delete();
            else
                deleteFiles(file);
        }
    }
}
