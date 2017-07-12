package com.kisi.acai.nfcreader.communication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by firta on 7/12/2017.
 */

public class ComResponse {

    @SerializedName("message")
    String message;

    public String getMessage()
    {
        return message;
    }
}
