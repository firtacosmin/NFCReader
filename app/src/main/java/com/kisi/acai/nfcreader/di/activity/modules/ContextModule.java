package com.kisi.acai.nfcreader.di.activity.modules;

import android.content.Context;

import com.kisi.acai.nfcreader.di.activity.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by firta on 7/12/2017.
 */

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context c){
        context = c;
    }

    @Provides
    @ActivityScope
    public Context provideContext(){
        return context;
    }

}
