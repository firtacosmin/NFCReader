package com.kisi.acai.nfcreader.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;


/**
 * Created by firta on 7/12/2017.
 * class that will print a gif into a view
 */
public class GifImageView extends View {



    /**
     * a listener interface that will be used to announce any interested
     * object when the animation has ended
     */
    public interface AnimationEndListener{
        void animationEnded();
    }

    private InputStream mInputStream;
    private Movie mMovie;
    private int mWidth, mHeight;
    private long mStart;
    private Context mContext;

    private boolean stopAtEnd = false;


    /**
     * A weak reference to the listener for the end of the animation
     */
    private WeakReference<AnimationEndListener> animationEndListenerWeakReference = new WeakReference<>(null);


    public GifImageView(Context context) {
        super(context);
        this.mContext = context;
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (attrs.getAttributeName(1).equals("background")) {
            int id = Integer.parseInt(attrs.getAttributeValue(1).substring(1));
            setGifImageResource(id);
        }
    }

    /**
     * setter method for the animation end listener;
     * the listener will be saved into a {@link WeakReference}
     * @param animationEndListener the listener to be saved
     */
    public void setAnimationEndListener(AnimationEndListener animationEndListener) {
        this.animationEndListenerWeakReference = new WeakReference<>(animationEndListener);
    }


    /**
     * setter for the stop at the end flag.
     * @param stopAtEnd if true then the gif will stop at the end and will announce the end of the
     *                  gif using {@link AnimationEndListener}
     */
    public void setStopAtEnd(boolean stopAtEnd) {
        this.stopAtEnd = stopAtEnd;
    }

    private void init() {
        setFocusable(true);
        mMovie = Movie.decodeStream(mInputStream);
        mWidth = mMovie.width();
        mHeight = mMovie.height();

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = SystemClock.uptimeMillis();

        if (mStart == 0) {
            mStart = now;
        }

        if (mMovie != null) {

            int duration = mMovie.duration();
            if (duration == 0) {
                duration = 1000;
            }
            int relTime = duration;
            boolean willInvalidate = true;

            /* test for end of the video*/
            if ( now - mStart >= duration && stopAtEnd ){
                /*if reached the end and stopAtEnd is set then stop the drawing*/
                relTime = duration;
                /* no need to invalidate if ended the animation*/
                willInvalidate = false;
                announceEndOfAnimation();
            }else{
                /*if the animation has not ended or the flag is not set then restart the or resume it*/
                relTime = (int) ((now - mStart) % duration);
            }


            mMovie.setTime(relTime);

            mMovie.draw(canvas, 0, 0);
            if ( willInvalidate ) {
                invalidate();
            }

        }
    }

    private void announceEndOfAnimation() {

        AnimationEndListener listener = animationEndListenerWeakReference.get();
        if ( listener != null ){
            listener.animationEnded();
        }


    }

    public void setGifImageResource(int id) {
        mInputStream = mContext.getResources().openRawResource(id);
        init();
    }

    public void setGifImageUri(Uri uri) {
        try {
            mInputStream = mContext.getContentResolver().openInputStream(uri);
            init();
        } catch (FileNotFoundException e) {
            Log.e("GIfImageView", "File not found");
        }
    }
}