package com.webkul.sociallogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aastha.gupta on 11/11/17 in prestashop_themes.
 */

public class CustomFacebookLoginButton extends android.support.v7.widget.AppCompatImageButton {
    private boolean isInitialized = false;
    private ProgressDialog mProgressDialog;
    private Fragment mainContext;
    private Activity activityMainContext;
    private SocialLoginCallback resultCallback;
    private boolean debug;
    private String TAG = "CustomFacebookLogin";

    private CallbackManager mFacebookCallbackManager;

    public CustomFacebookLoginButton(Context context) {
        this(context, null);
    }

    public CustomFacebookLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFacebookLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void initializeFacebook(Activity mainContext, SocialLoginCallback resultCallback, boolean debug) {
        this.activityMainContext = mainContext;
        this.resultCallback = resultCallback;
        this.debug = debug;
        mFacebookCallbackManager = CallbackManager.Factory.create();
        isInitialized = true;
        if (debug) {
            Log.d(TAG, "initializeFacebook: initialization started");
        }
    }

    public void initializeFacebook(Fragment mainContext, SocialLoginCallback resultCallback, boolean debug) {
        this.mainContext = mainContext;
        this.resultCallback = resultCallback;
        this.debug = debug;
        mFacebookCallbackManager = CallbackManager.Factory.create();
        isInitialized = true;
        if (debug) {
            Log.d(TAG, "initializeFacebook: initialization started");
        }
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
//        setImageResource(R.drawable.ic_vector_facebook);
//        setBackgroundColor(Color.TRANSPARENT);
//        setPadding(10, 10, 10, 10);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInitialized) {
                    mProgressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.please_wait), getResources().getString(R.string.processing_request_response), true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    if (mainContext == null) {
                        LoginManager.getInstance().logInWithReadPermissions(activityMainContext, Arrays.asList("email"));
                    } else {
                        LoginManager.getInstance().logInWithReadPermissions(mainContext, Arrays.asList("email"));
                    }
                    LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        JSONObject Pic = object.getJSONObject("picture");
                                        JSONObject data = Pic.getJSONObject("data");
                                        SocialLoginUser socialLoginUser = new SocialLoginUser(
                                                object.getString("first_name"), object.getString("last_name"),
                                                object.getString("email"), data.getString("url"));
                                        LoginManager.getInstance().logOut();
                                        if (debug) {
                                            Log.d(TAG, "onSuccess: " + socialLoginUser.toString());
                                        }
                                        mProgressDialog.dismiss();
                                        resultCallback.onResult(socialLoginUser);

                                    } catch (Exception e) {
                                        mProgressDialog.dismiss();
                                        LoginManager.getInstance().logOut();
                                        e.printStackTrace();
                                    }
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,first_name,last_name,picture.type(large)");
                            graphRequest.setParameters(parameters);
                            graphRequest.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            if (debug) {
                                Log.d(TAG, "onCancel: request cancelled.");
                            }
                            mProgressDialog.dismiss();
                            resultCallback.onCancel();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            if (debug) {
                                Log.d(TAG, "onError: " + exception.getLocalizedMessage());
                            }
                            exception.printStackTrace();
                            mProgressDialog.dismiss();
                            resultCallback.onError();
                        }
                    });
                } else {
                    if (debug) {
                        Log.d(TAG, "NOT INITIALIZED");
                    }
                    Log.e("SOCIAL LOGIN", "FACEBOOK NOT INITIALIZED");
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (debug) {
            Log.d(TAG, "onActivityResult : RESULT_CODE = " + resultCode);
            Utils.BundleLogger(data.getExtras());
        }
        if (resultCode == RESULT_OK) {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            mProgressDialog.dismiss();
        }
    }
}
