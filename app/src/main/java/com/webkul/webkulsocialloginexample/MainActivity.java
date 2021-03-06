package com.webkul.webkulsocialloginexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.webkul.sociallogin.CustomFacebookLoginButton;
import com.webkul.sociallogin.CustomTwitterButton;
import com.webkul.sociallogin.GoogleLoginButton;
import com.webkul.sociallogin.GoogleUtils;
import com.webkul.sociallogin.InstagramLoginButton;
import com.webkul.sociallogin.SocialLoginCallback;
import com.webkul.sociallogin.SocialLoginUser;

public class MainActivity extends AppCompatActivity implements SocialLoginCallback {
    private CustomTwitterButton mTwitterLoginButton;
    private GoogleLoginButton googleLoginButton;
    private CustomFacebookLoginButton facebookLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To be called even before setContentView() is called
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                SocialLoginConfig.TWITTER_KEY, SocialLoginConfig.TWITTER_SECRET);

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_main);

        mTwitterLoginButton = findViewById(R.id.twitter_login_button);
        InstagramLoginButton instagramLoginButton = findViewById(R.id.instagram_sign_in_button);
        facebookLoginButton = findViewById(R.id.facebook_login);
        googleLoginButton = findViewById(R.id.google_sign_in_button);

        mTwitterLoginButton.initialize(this, true);
        instagramLoginButton.initialize(this, SocialLoginConfig.INSTA_CLIENT_ID,
                SocialLoginConfig.INSTA_CLIENT_SECRET, SocialLoginConfig.INSTA_REDIRECT_URI, this, true);
        facebookLoginButton.initializeFacebook(this, this, true);
        googleLoginButton.initialize(this, getString(R.string.default_web_client_id), this, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GoogleUtils.RC_GOOGLE_SIGN_IN) {
                googleLoginButton.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
                mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
            } else {
                facebookLoginButton.onActivityResult(requestCode, resultCode, data);
            }
    }

    @Override
    public void onResult(SocialLoginUser socialLoginUser) {
        ProgressDialog mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.processing_request_response), true);
        mProgressDialog.setCanceledOnTouchOutside(false);
//        HashMap<String, String> parameters = new HashMap<>();
//        parameters.put("firstName", socialLoginUser.getFirstName());
//        parameters.put("lastName", socialLoginUser.getLastName());
//        parameters.put("email", socialLoginUser.getEmail());
        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
        //Your server side code for login
        mProgressDialog.dismiss();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError() {

    }
}
