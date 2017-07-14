package com.kisi.acai.nfcreader.di.activity.modules;

import android.content.Context;
import android.nfc.NfcAdapter;

import com.kisi.acai.nfcreader.di.activity.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by firta on 7/14/2017.
 */

@Module
public class NfcAdapterModule {

    @Provides
    @ActivityScope
    public NfcAdapter provideAdapter(Context context){
        return NfcAdapter.getDefaultAdapter(context);


    }

}
