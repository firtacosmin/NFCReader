package com.kisi.acai.nfcreader.di.application.modules;

import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.di.activity.ActivityScope;
import com.kisi.acai.nfcreader.di.application.ApplicationScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by firta on 7/12/2017.
 */

@Module
public class ComModelModule {

    @Provides
    @ApplicationScope
    public ComModel provideModel(){
        return new ComModel();
    }
}
