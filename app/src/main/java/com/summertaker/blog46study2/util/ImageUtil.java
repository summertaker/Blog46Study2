package com.summertaker.blog46study2.util;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

/*
 * https://medium.com/@rajeefmk/android-textview-and-image-loading-from-url-part-2-13eccaf27d9c
 */
public class ImageUtil {

    /*
    This method will create a spannable which is a displayable styled text. It
    also has a custom ImageGetter based on Picasso for loading <img> tags inside the html.
    We use this for rendering formulas in challenges
     */

    public static Spannable getSpannableHtmlWithImageGetter(Context context, TextView textView, String value) {
        PicassoImageGetter imageGetter = new PicassoImageGetter(context, textView);
        //GlideImageGetter imageGetter = new GlideImageGetter(context, textView);

        Spannable html;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(value, imageGetter, null);
        }
        return html;
    }

    /*
    Used for setting click listener on the formulas loaded in textview / html. This is
    done using ImageSpan which detects the Image content inside the spannable.
    After that it sets a onClick listener using URLSpan. This is done for all the <img> inside
    the html.
     */
    public static void setClickListenerOnHtmlImageGetter(Spannable html, final Callback callback, boolean b) {
        for (final ImageSpan span : html.getSpans(0, html.length(), ImageSpan.class)) {
            int flags = html.getSpanFlags(span);
            int start = html.getSpanStart(span);
            int end = html.getSpanEnd(span);

            html.setSpan(new URLSpan(span.getSource()) {
                @Override
                public void onClick(View v) {
                    callback.onImageClick(span.getSource());
                }
            }, start, end, flags);
        }
    }

    public interface Callback {
        void onImageClick(String imageUrl);
    }
}
