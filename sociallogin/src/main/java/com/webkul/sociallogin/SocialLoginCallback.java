package com.webkul.sociallogin;

/**
 * Created by aastha.gupta on 13/11/17 in prestashop_themes.
 */

public interface SocialLoginCallback {

    void onResult(SocialLoginUser user);

    void onCancel();

    void onError();
}
