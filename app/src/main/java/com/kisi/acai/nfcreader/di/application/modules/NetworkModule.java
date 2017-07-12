package com.kisi.acai.nfcreader.di.application.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kisi.acai.nfcreader.BuildConfig;
import com.kisi.acai.nfcreader.communication.model.ComEndpointInterface;
import com.kisi.acai.nfcreader.di.application.ApplicationScope;
import com.kisi.acai.nfcreader.util.PlatformSettings;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by firta on 7/12/2017.
 * This is the module that contains all the network objects
 */

@Module
public class NetworkModule {

    @ApplicationScope
    @Provides
    public HttpLoggingInterceptor provideLoggingInterceptor(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return  logging;
    }
    @ApplicationScope
    @Provides
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor logging){
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }
    @ApplicationScope
    @Provides
    public Gson provideGson(){
        return  new GsonBuilder().create();
    }

    @ApplicationScope
    @Provides
    public Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson){
        Retrofit r = new Retrofit.Builder()
                .baseUrl(PlatformSettings.getUnlockUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        return r;
    }


    @Provides
    @ApplicationScope
    public ComEndpointInterface provideComEndpointInterface(@ApplicationScope Retrofit retrofit){
        return retrofit.create(ComEndpointInterface.class);
    }

}
