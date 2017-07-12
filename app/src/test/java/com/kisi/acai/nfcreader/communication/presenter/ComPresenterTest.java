package com.kisi.acai.nfcreader.communication.presenter;

import android.os.Bundle;
import android.os.Handler;

import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.view.ComView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;

/**
 * Created by firta on 7/12/2017.
 *
 */
public class ComPresenterTest {


    private ComPresenter presenter;
    private ComModel model;
    private ComView view;

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

        presenter = new ComPresenter(model, view);


    }


    @Test
    public void newAppStart_PrintSplash() throws Exception {

        presenter.activityCreated(null);
        presenter.activityResumed();
        Mockito.verify(view).showSplashScreen();
        presenter.splashFinished();
        Mockito.verify(view).showHome();

    }

    @Test
    public void appInBackground_PrintSplash() throws Exception {
        presenter.activityPaused();
        presenter.activityResumed();
        Mockito.verify(view).showSplashScreen();
        presenter.splashFinished();
        Mockito.verify(view).showHome();

    }

    @Test
    public void deviceRotates_PrintSplash() throws Exception {

        presenter.activityPaused();
        presenter.activityCreated(new Bundle());
        presenter.activityResumed();
        Mockito.verify(view, never()).showSplashScreen();
        Mockito.verify(view).showHome();

    }
}