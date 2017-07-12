package com.kisi.acai.nfcreader.di.activity;

import android.content.Context;

import com.kisi.acai.nfcreader.MainActivity;
import com.kisi.acai.nfcreader.di.activity.modules.ComModelModule;
import com.kisi.acai.nfcreader.di.activity.modules.ComViewModule;
import com.kisi.acai.nfcreader.di.activity.modules.ContextModule;

import dagger.Component;

/**
 * Created by firta on 7/12/2017.
 */

@ActivityScope
@Component (modules={ContextModule.class, ComViewModule.class, ComModelModule.class})
public interface ActivityComponent {

    Context getContext();

    void bind(MainActivity act);

}
