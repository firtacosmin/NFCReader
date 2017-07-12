package com.kisi.acai.nfcreader.util;

/**
 * Created by firta on 7/12/2017.
 * A class that contains all platform URLs
 */

public class PlatformSettings {

    private static final String UNLOCK_URL = "https://api.getkisi.com/locks/5124/";


    public static String getUnlockUrl(){
        return UNLOCK_URL;
    }
}
