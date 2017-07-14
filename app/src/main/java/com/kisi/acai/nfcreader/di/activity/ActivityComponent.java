package com.kisi.acai.nfcreader.di.activity;

import android.content.Context;

import com.kisi.acai.nfcreader.MainActivity;
import com.kisi.acai.nfcreader.di.activity.modules.NfcAdapterModule;
import com.kisi.acai.nfcreader.di.application.ApplicationScope;
import com.kisi.acai.nfcreader.di.application.modules.ComModelModule;
import com.kisi.acai.nfcreader.di.activity.modules.ComViewModule;
import com.kisi.acai.nfcreader.di.activity.modules.ContextModule;
import com.kisi.acai.nfcreader.di.application.ApplicationComponent;
import com.kisi.acai.nfcreader.di.application.modules.NetworkModule;

import dagger.Component;

/**
 * Created by firta on 7/12/2017.
 */

@ActivityScope
@Component (modules={ContextModule.class, ComViewModule.class, NfcAdapterModule.class}, dependencies = ApplicationComponent.class)
public interface ActivityComponent {

    Context getContext();

    void bind(MainActivity act);

}
