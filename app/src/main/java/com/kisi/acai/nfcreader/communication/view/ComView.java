package com.kisi.acai.nfcreader.communication.view;

import com.kisi.acai.nfcreader.communication.model.ComModel;

/**
 * Created by firta on 7/12/2017.
 * The interface that will be implemented by the communication view and will be used by the
 * ComPresenter to display information
 */

public interface ComView {
    void showSplashScreen();

    void showHome();

    void showUser(ComModel.User user);

    void showUnlockAnimation();
}
