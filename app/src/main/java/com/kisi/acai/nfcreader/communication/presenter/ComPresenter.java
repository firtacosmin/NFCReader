package com.kisi.acai.nfcreader.communication.presenter;

import android.content.Intent;
import android.os.Bundle;

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

    @Inject
    public ComPresenter(@ApplicationScope ComModel model, @ActivityScope ComView view){

        this.model = model;
        this.view = view;
    }

    public void activityResumed(){

    }

    public void activityPaused(){

    }

    public void activitySaveInstanceState(Bundle instance){

    }

    public void processViewIntent(Intent intent){

    }

    public void activityCreated(Bundle savedInstanceState){

    }




}
