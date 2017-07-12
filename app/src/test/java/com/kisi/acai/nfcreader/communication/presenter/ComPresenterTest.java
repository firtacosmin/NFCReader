package com.kisi.acai.nfcreader.communication.presenter;

import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.view.ComView;

import org.junit.Before;
import org.mockito.Mockito;

import static org.junit.Assert.*;

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
}