package com.kisi.acai.nfcreader.di.activity.modules;

import com.kisi.acai.nfcreader.communication.view.ComView;
import com.kisi.acai.nfcreader.di.activity.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by firta on 7/12/2017.
 */

@Module
public class ComViewModule {

    private final ComView view;
    public  ComViewModule(ComView com){
        view = com;
    }

    @Provides
    @ActivityScope
    public ComView provideComView(){
        return view;
    }
}
