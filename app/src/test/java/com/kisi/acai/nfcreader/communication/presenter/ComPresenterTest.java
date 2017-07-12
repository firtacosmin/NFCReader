package com.kisi.acai.nfcreader.communication.presenter;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;

import com.kisi.acai.nfcreader.communication.model.ComEndpointInterface;
import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.view.ComView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by firta on 7/12/2017.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Ndef.class, Log.class})
public class ComPresenterTest {


    private ComPresenter presenter;
    private ComModel model;
    private ComView view;
    private Intent intent;
    private Tag tag;
    private Ndef ndef;
    private NdefMessage ndefMessage;
    private NdefRecord ndefRecord;
    private byte[] nothingBytes;
    private byte[] unlockBytes;

    /**
     *
     * 1. when app starts with no saved instance state or resumes with no saved instance state ( not from rotation ) while not showing user info
     *          -> show the loading screen
     *          -> show the home screen
     * 2. when app starts with saved instance state while not showing user info
     *          -> do not show loading screen
     *          -> show the home screen
     * 3. when intent is received with "unlock"
     *          -> send request
     *          -> show animation
     *          -> show user info
     * 4. when intent is received with "nothing"
     *          -> do not show user info
     * 5. when intent is received while showing user info
     *          -> do nothing
     * 6. when app starts with no saved instance state or resumes with no saved instance state ( not from rotation ) while showing user info
     *          -> show the loading screen
     *          -> go to user info
     * 7. when app starts with saved instance state while not showing user info
     *          -> do not show loading screen
     *          -> show the user info
     *
     *
     *
     */

    @Before
    public void setUp() throws Exception {

        model = Mockito.mock(ComModel.class);
        view = Mockito.mock(ComView.class);

        intent = Mockito.mock(Intent.class);
        tag = Mockito.mock(Tag.class);
        ndef = Mockito.mock(Ndef.class);
        ndefMessage = Mockito.mock(NdefMessage.class);
        ndefRecord = Mockito.mock(NdefRecord.class);

        nothingBytes = "nothing".getBytes();
        unlockBytes = "unlock".getBytes();

        presenter = new ComPresenter(model, view);


    }


    @Test
    public void newAppStart_PrintSplash() throws Exception {

        PowerMockito.mockStatic(Log.class);
        presenter.activityCreated(null);
        presenter.activityResumed();
        Mockito.verify(view).showSplashScreen();
        presenter.splashFinished();
        Mockito.verify(view).showHome();

    }

    @Test
    public void appInBackground_PrintSplash() throws Exception {
        PowerMockito.mockStatic(Log.class);
        presenter.activityPaused();
        presenter.activityResumed();
        Mockito.verify(view).showSplashScreen();
        presenter.splashFinished();
        Mockito.verify(view).showHome();

    }

    @Test
    public void deviceRotates_PrintSplash() throws Exception {

        PowerMockito.mockStatic(Log.class);
        presenter.activityPaused();
        presenter.activityCreated(new Bundle());
        presenter.activityResumed();
        Mockito.verify(view, never()).showSplashScreen();
        Mockito.verify(view).showHome();

    }


    @Test
    public void receivedIntentWithNothing() throws Exception{

        mockForIntent(nothingBytes);

        presenter.processViewIntent(intent);
        Mockito.verify(view, never()).showUser(model.getUser());

    }

    @Test
    public void receivedIntentWithUnlock() throws Exception{
        mockForIntent(unlockBytes);

        presenter.processViewIntent(intent);
        Mockito.verify(view).showUnlockAnimation();
        Mockito.verify(view).showUser(model.getUser());

    }

    @Test
    public void receiveNothingIntentWhileUserUnlocked() throws  Exception{
        mockForIntent(nothingBytes);
        presenter.setUnlocked();
//        when(presenter.isUnlocked()).thenReturn(true);
        presenter.processViewIntent(intent);
        Mockito.verify(view, never()).showSplashScreen();
        Mockito.verify(view, never()).showHome();
        Mockito.verify(view, never()).showUser((ComModel.User) any());
        Mockito.verify(view, never()).showUnlockAnimation();

    }

    @Test
    public void receiveUnlockIntentWhileUserUnlocked() throws  Exception{
        mockForIntent(unlockBytes);
        presenter.setUnlocked();
//        when(presenter.isUnlocked()).thenReturn(true);
        presenter.processViewIntent(intent);
        Mockito.verify(view, never()).showSplashScreen();
        Mockito.verify(view, never()).showHome();
        Mockito.verify(view, never()).showUser((ComModel.User) any());
        Mockito.verify(view, never()).showUnlockAnimation();

    }


    private void mockForIntent(byte[] message){
        PowerMockito.mockStatic(Ndef.class);
        PowerMockito.mockStatic(Log.class);
        when(intent.getAction()).thenReturn(NfcAdapter.ACTION_NDEF_DISCOVERED);
        when(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)).thenReturn(tag);
        when(Ndef.get((Tag) any())).thenReturn(ndef);
        when(Log.d(any(String.class), any(String.class))).thenReturn(0);
        when(ndef.getCachedNdefMessage()).thenReturn(ndefMessage);
        when(ndefMessage.getRecords()).thenReturn(new NdefRecord[]{ndefRecord});
        when(ndefRecord.getPayload()).thenReturn(message);
    }





}