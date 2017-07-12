package com.kisi.acai.nfcreader;

import android.app.Application;

import com.kisi.acai.nfcreader.di.application.ApplicationComponent;
import com.kisi.acai.nfcreader.di.application.DaggerApplicationComponent;

/**
 * Created by firta on 7/12/2017.
 * The main application object
 */

public class MainApp extends Application {


    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerApplicationComponent.builder().build();


    }




    public ApplicationComponent getComponent() {
        return component;
    }

}
