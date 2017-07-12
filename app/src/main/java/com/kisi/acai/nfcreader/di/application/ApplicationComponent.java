package com.kisi.acai.nfcreader.di.application;

import com.kisi.acai.nfcreader.communication.model.ComEndpointInterface;
import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.di.application.modules.ComModelModule;
import com.kisi.acai.nfcreader.di.application.modules.NetworkModule;

import dagger.Component;
import dagger.Module;

/**
 * Created by firta on 7/12/2017.
 */

@ApplicationScope
@Component(modules = {ComModelModule.class, NetworkModule.class})
public interface ApplicationComponent {

    ComEndpointInterface comEndPointInterface();
    ComModel comModel();

}
