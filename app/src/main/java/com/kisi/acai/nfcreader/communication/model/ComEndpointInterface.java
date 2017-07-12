package com.kisi.acai.nfcreader.communication.model;


import com.kisi.acai.nfcreader.util.IHttpConsts;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by firta on 7/12/2017.
 * the interface that will be used by {@link retrofit2.Retrofit} to send request
 */

public interface ComEndpointInterface {

    @Headers("Authorization: KISI-LINK 75388d1d1ff0dff6b7b04a7d5162cc6c ")
    @POST(IHttpConsts.COM_UNLOCK_PATH)
    Call<ComResponse> sendComRequest();

}
