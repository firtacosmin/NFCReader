package com.kisi.acai.nfcreader.communication.card;

import android.nfc.tech.IsoDep;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by firta on 7/14/2017.
 * This is the class that manages the communication with the NFC Card
 */

public class CardComTransceiver implements Runnable {


    public static final String RECEIVE_HELLO = "HELLO";
    public static final String SEND_START = "KNOCK KNOCK";
    public static final String RECEIVE_1 = "WHO'S THERE?";
    public static final String SEND_1 = "I AM GROOT!";
    public static final String NOTHING = "nothing";
    public static final String UNLOCK = "unlock";

    private static final byte[] CLA_INS_P1_P2 = { 0x00, (byte)0xA4, 0x04, 0x00 };
    private static final byte[] AID_ANDROID = { (byte)0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };

    public interface OnMessageReceived {


        void onUnlock();

        void onError(Exception exception);

        void onNothing();

    }


    private static final String TAG = "CardComTransceiver";


    private IsoDep isoDep;

    private boolean running = false;
    private final OnMessageReceived onMessageReceived;


    public CardComTransceiver(IsoDep isoDep, OnMessageReceived onMessageReceived) {
        this.isoDep = isoDep;
        this.onMessageReceived = onMessageReceived;
    }


    public void start(){
        if (!running) {
            running = true;
            Thread thread = new Thread(this);
            thread.start();
        }
    }


    @Override
    public void run() {
        try {
            isoDep.connect();
            byte[] response = isoDep.transceive(createSelectAidApdu(AID_ANDROID));
            while (isoDep.isConnected() && !Thread.interrupted() && running) {
                byte[] message = getNextMessage(response);
                response = isoDep.transceive(message);
            }
            isoDep.close();
            running = false;
        }
        catch (IOException e) {
            onMessageReceived.onError(e);
        }
    }

    private byte[] createSelectAidApdu(byte[] aid) {
        Log.d(TAG,"::createSelectAidApdu");
        byte[] result = new byte[6 + aid.length];
        System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
        result[4] = (byte)aid.length;
        System.arraycopy(aid, 0, result, 5, aid.length);
        result[result.length - 1] = 0;
        return result;

    }

    private byte[] getNextMessage(byte[] received){
        String textEncoding = "UTF-8";

        // Get the Text
        byte[] ret = {};
        try {
            String recMsj = new String(received, 0, received.length, textEncoding);
            Log.d(TAG,"::getNextMessage got: "+recMsj);
            switch (recMsj) {
                case RECEIVE_HELLO:
                    Log.d(TAG,"::getNextMessage received hello. will return: "+SEND_START);
                    ret = SEND_START.getBytes(textEncoding);
                case RECEIVE_1:
                    Log.d(TAG,"::getNextMessage received "+RECEIVE_1+". will return: "+SEND_1);
                    ret = SEND_1.getBytes(textEncoding);
                    break;
                case NOTHING:
                    Log.d(TAG,"::getNextMessage received "+NOTHING);
                    onMessageReceived.onNothing();
                    running = true;
                    break;
                case UNLOCK:
                    Log.d(TAG,"::getNextMessage received "+UNLOCK);
                    onMessageReceived.onUnlock();
                    running = true;
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG,"::getNextMessage caught exception ");
            e.printStackTrace();
            onMessageReceived.onError(e);
            running = true;
        }
        return ret;

    }
}
