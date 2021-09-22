package com.jince.emchat;

public interface EMLoginListener {

    void onLoginSuccess();

    void onLoginFailed(int code, String message);
}
