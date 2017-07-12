package com.kisi.acai.nfcreader.communication.model;

import javax.inject.Inject;

/**
 * Created by firta on 7/12/2017.
 * This class will do the model actions for the communication view
 * Actions :
 *  - provide user information
 *  - send http request to server
 */

public class ComModel {

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
    public ComModel(){


    }


    public User getUser(){
        return user;
    }
}
