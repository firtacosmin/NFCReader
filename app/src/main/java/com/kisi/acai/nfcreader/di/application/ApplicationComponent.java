package com.kisi.acai.nfcreader.di.application;

import com.kisi.acai.nfcreader.di.application.modules.ComModelModule;

import dagger.Component;
import dagger.Module;

/**
 * Created by firta on 7/12/2017.
 */

@ApplicationScope
@Component(modules = {ComModelModule.class})
public interface ApplicationComponent {
}
