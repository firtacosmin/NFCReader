package com.kisi.acai.nfcreader.communication.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.view.ComView;
import com.kisi.acai.nfcreader.di.activity.ActivityScope;
import com.kisi.acai.nfcreader.di.application.ApplicationScope;

import javax.inject.Inject;

/**
 * Created by firta on 7/12/2017.
 * This is the presenter class that will get the information from NFC and will decide what actions
 * will be taken and what will be displayed in the view depending on the information in the nfc payload
 */

public class ComPresenter {

    private final ComModel model;
    private final ComView view;


    /**
     * A flag that if it is true will make
     * the {@link #activityResumed()} method to call {@link ComView#showSplashScreen()}
     */
    private boolean willShowSplash = true;

    @Inject
    public ComPresenter(@ApplicationScope ComModel model, @ActivityScope final ComView view){


        this.model = model;
        this.view = view;



    }

    public void activityResumed(){
        if ( willShowSplash ){
            view.showSplashScreen();
        }
    }

    public void activityPaused(){
        /*if the activity is paused then it could be because the home button has been pressed,
        then will display the splash at resume*/
        willShowSplash = true;

    }

    public void activitySaveInstanceState(Bundle instance){

    }

    public void processViewIntent(Intent intent){

    }

    public void activityCreated(Bundle savedInstanceState){
        if ( savedInstanceState == null ){
            /*new activity creation*/
            willShowSplash = true;
        }else{
            willShowSplash = false;
        }
    }


    public void splashFinished() {

        view.showHome();


    }
}
