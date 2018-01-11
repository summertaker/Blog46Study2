package com.summertaker.blog46study2.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.summertaker.blog46study2.R;

public class PicassoImageGetter implements Html.ImageGetter {

    private Context mContext;
    private TextView mTextView = null;

    public PicassoImageGetter() {

    }

    public PicassoImageGetter(Context context, TextView target) {
        mContext = context;
        mTextView = target;
    }

    @Override
    public Drawable getDrawable(String source) {
        BitmapDrawablePlaceHolder drawable = new BitmapDrawablePlaceHolder();
        //drawable.setDrawable(mContext.getDrawable(R.drawable.placeholder));

        if (source != null && !source.isEmpty()) {
            if (source.toLowerCase().contains(".gif")) {
                //Glide.with(mContext).load(source).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(drawable);
            } else {
                //Log.e("PICASSO", "source: " + source);
                Picasso.with(mContext).load(source).placeholder(R.drawable.placeholder).into(drawable);
            }
        }
        return drawable;
    }

    private class BitmapDrawablePlaceHolder extends BitmapDrawable implements Target {

        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, width, height);
            setBounds(0, 0, width, height);

            if (mTextView != null) {
                mTextView.setText(mTextView.getText());
            }

            // https://www.codeday.top/2017/07/30/31369.html
            //checkBounds();
        }

        private void checkBounds() {
            float defaultProportion = (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
            int width = Math.min(mTextView.getWidth(), drawable.getIntrinsicWidth());
            int height = (int) ((float) width / defaultProportion);

            if (getBounds().right != mTextView.getWidth() || getBounds().bottom != height) {

                setBounds(0, 0, mTextView.getWidth(), height); //set to full width

                int halfOfPlaceHolderWidth = (int) ((float) getBounds().right / 2f);
                int halfOfImageWidth = (int) ((float) width / 2f);

                drawable.setBounds(
                        halfOfPlaceHolderWidth - halfOfImageWidth, //centering an image
                        0,
                        halfOfPlaceHolderWidth + halfOfImageWidth,
                        height);

                mTextView.setText(mTextView.getText()); //refresh text
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setDrawable(new BitmapDrawable(mContext.getResources(), bitmap));

            /*
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenWidth = size.x;
                //int screenHeight = size.y;

                //float ratio = screenWidth / bitmap.getWidth();

                int height = (screenWidth / bitmap.getWidth()) * bitmap.getHeight();

                drawable.setBounds(0, 0, screenWidth, height);
                setBounds(0, 0, screenWidth, height);
            }
            */
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.e("", "PicassoImageGetter.onBitmapFailed()....");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
