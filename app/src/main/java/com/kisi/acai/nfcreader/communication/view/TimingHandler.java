package com.kisi.acai.nfcreader.communication.view;

import android.os.Handler;
import android.os.Message;

import com.kisi.acai.nfcreader.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by firta on 7/13/2017.
 */

public class TimingHandler extends Handler {

    public static final long SPLASH_DELAY = 1000;
    private static final int STOP_SPLASH = 1;

    private WeakReference<MainActivity> activityWR = new WeakReference<MainActivity>(null);

    public void setActivity(MainActivity act){
        activityWR = new WeakReference<>(act);
    }



    @Override
    public void handleMessage(Message msg) {
        if ( msg.what == STOP_SPLASH ){
            MainActivity act = activityWR.get();
            if ( act != null ){
                act.timerTicked();
            }
        }
    }

}
