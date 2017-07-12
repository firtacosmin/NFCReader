package com.kisi.acai.nfcreader.di.activity.modules;

import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.di.activity.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by firta on 7/12/2017.
 */

@Module
public class ComModelModule {

    @Provides
    @ActivityScope
    public ComModel provideModel(){
        return new ComModel();
    }
}
