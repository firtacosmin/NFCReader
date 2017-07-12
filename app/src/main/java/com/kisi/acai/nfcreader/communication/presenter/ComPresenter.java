package com.kisi.acai.nfcreader.communication.presenter;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.kisi.acai.nfcreader.communication.model.ComEndpointInterface;
import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.view.ComView;
import com.kisi.acai.nfcreader.di.activity.ActivityScope;
import com.kisi.acai.nfcreader.di.application.ApplicationScope;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

/**
 * Created by firta on 7/12/2017.
 * This is the presenter class that will get the information from NFC and will decide what actions
 * will be taken and what will be displayed in the view depending on the information in the nfc payload
 */

public class ComPresenter {

    private static final String TAG = "ComPresenter";
    private final ComModel model;
    private final ComView view;
    private ComEndpointInterface endPoint;

    /**
     * the payload to search for
     */
    private static final String UNLOCK_MSJ = "unlock";
    private static final String NOTHING_MSJ = "nothing";

    /**
     * A flag that if it is true will make
     * the {@link #activityResumed()} method to call {@link ComView#showSplashScreen()}
     */
    private boolean willShowSplash = true;

    /**
     * will be set to true when an "unlock" message is received
     * It will move the view to user view after the splash screen
     */
    private boolean unlocked = false;


    @Inject
    public ComPresenter(@ApplicationScope ComModel model, @ActivityScope final ComView view){


        this.model = model;
        this.view = view;
        this.endPoint = endPoint;
    }

    public void activityResumed(){
        Log.d(TAG,"::activityResumed");
        if ( willShowSplash ){
            Log.d(TAG,"::activityResumed will show splas");
            view.showSplashScreen();
        }else{
            Log.d(TAG,"::activityResumed will show home");
            view.showHome();
        }
    }

    public void activityPaused(){
        Log.d(TAG,"::activityPaused");
        /*if the activity is paused then it could be because the home button has been pressed,
        then will display the splash at resume*/
        willShowSplash = true;

    }

    public void activitySaveInstanceState(Bundle instance){

        Log.d(TAG,"::activitySaveInstanceState");
    }

    public void activityCreated(Bundle savedInstanceState){
        Log.d(TAG,"::activityCreated");
        if ( savedInstanceState == null ){
            Log.d(TAG,"::activityCreated set splash = true");
            /*new activity creation*/
            willShowSplash = true;
        }else{
            Log.d(TAG,"::activityCreated set splash = false");
            willShowSplash = false;
        }
    }

    public void splashFinished() {
        Log.d(TAG,"::splashFinished");
        willShowSplash = false;
        if ( isUnlocked() ){
            view.showUser(model.getUser());
        }else {
            view.showHome();
        }
    }


    public void processViewIntent(Intent intent){
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            Parcelable[] rawMessages =
//                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            if (rawMessages != null) {
//                NdefMessage[] messages = new NdefMessage[rawMessages.length];
//                for (int i = 0; i < rawMessages.length; i++) {
//                    messages[i] = (NdefMessage) rawMessages[i];
//                }
//                // Process the messages array.
//            }


            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return ;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
//                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    updateViewForPayload(readText(ndefRecord));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unsupported Encoding", e);
                }
//                }
            }
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        Log.d(TAG,"::other option: "+new String(payload, 0, payload.length  - 1, textEncoding));
        return new String(payload, 0, payload.length, textEncoding);
    }


    /**
     * method that will decide what to display on the view depending on the payload value
     * @param payload the payload receiver on the intent
     */
    private void updateViewForPayload(String payload){

        if ( !isUnlocked() && payload.contains(UNLOCK_MSJ) ){
            view.showUnlockAnimation();
            view.showUser(model.getUser());
            setUnlocked();
        }else{
            view.showNothingMessage();
        }
        model.announceUnlockToServer();


    }

    public boolean isUnlocked(){
        return unlocked;
    }

    public void setUnlocked(){
        unlocked = true;
    }
    public void resetUnlocked(){
        unlocked = false;
    }


    public void logoutPressed() {
        view.showHome();
        resetUnlocked();

    }
}
