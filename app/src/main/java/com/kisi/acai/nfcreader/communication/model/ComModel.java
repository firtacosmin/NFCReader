package com.kisi.acai.nfcreader.communication.model;

import android.util.Log;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by firta on 7/12/2017.
 * This class will do the model actions for the communication view
 * Actions :
 *  - provide user information
 *  - send http request to server
 */

public class ComModel {

    private static final String TAG = "ComModel";
    private ComEndpointInterface api;

    public class User{
        private String username;
        private String email;

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    private User user = new User("Miguel", "miguel@gmail.com");

    @Inject
    public ComModel(ComEndpointInterface api){
        this.api = api;
    }


    public User getUser(){
        return user;
    }

    /**
     * method that will use {@link ComEndpointInterface} to send a request to the server
     */
    public void announceUnlockToServer(){
        Log.d(TAG,"::announceUnlockToServer ");

        Call<ComResponse> call = api.sendComRequest();
        call.enqueue(new Callback<ComResponse>() {
            @Override
            public void onResponse(Call<ComResponse> call, Response<ComResponse> response) {
                Log.d(TAG,"::onResponse response:"+response.body());
                Log.d(TAG,"::onResponse error response:"+response.errorBody());
//                Log.d(call.request().headers().toString())
            }

            @Override
            public void onFailure(Call<ComResponse> call, Throwable t) {
                Log.d(TAG,"::onFailure");
            }
        });

    }
}
