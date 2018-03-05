package com.webkul.sociallogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by aastha.gupta on 12/12/16.
 */

public class CustomTwitterButton extends TwitterLoginButton {
    private static final String TAG = "CustomTwitterButton";
    Context mContext;
    private FirebaseAuth mAuthTwitter;
    private SocialLoginCallback resultCallback;
    private boolean debug;
    private boolean isInitialized;

    public CustomTwitterButton(Context context) {
        super(context, null);
        mContext = context;
        init();
    }

    public CustomTwitterButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        init();

    }

    public CustomTwitterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public void initialize(SocialLoginCallback resultCallback, boolean debug) {
        this.resultCallback = resultCallback;
        this.debug = debug;
        mAuthTwitter = FirebaseAuth.getInstance();
        isInitialized = true;
        if (debug) {
            Log.d(TAG, "initializeTwitter: initialization started");
        }
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
//        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vector_twitter, 0, 0, 0);
//        setBackgroundColor(Color.TRANSPARENT);
//        setPadding(10, 10, 10, 10);
//        setText("");
        setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                ProgressDialog mProgressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.please_wait),
                        getResources().getString(R.string.processing_request_response), true);
                if (isInitialized) {
                    handleTwitterSession(result.data, mProgressDialog);
                } else {
                    mProgressDialog.dismiss();
                    if (debug) {
                        Log.e("SOCIAL LOGIN", "TWITTER NOT INITIALIZED");
                    }
                }
            }

            @Override
            public void failure(TwitterException exception) {
                resultCallback.onError();
                FirebaseSignOut();
            }
        });
    }


    private void handleTwitterSession(TwitterSession session, final ProgressDialog mProgressDialog) {
        try {
            AuthCredential credential = TwitterAuthProvider.getCredential(
                    session.getAuthToken().token,
                    session.getAuthToken().secret);

            mAuthTwitter.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuthTwitter.getCurrentUser();
                                if (debug) {
                                    Log.d(TAG, "onComplete: Twitter Firebase user found");
                                }
                                getUserDetails(user, mProgressDialog);
                            } else {
                                Toast.makeText(mContext, "Login failed", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });
        } catch (RuntimeExecutionException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
//            Crashlytics.logException(e);
            FirebaseSignOut();
        }
    }

    private void FirebaseSignOut() {
        mAuthTwitter.signOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    private void getUserDetails(FirebaseUser user, ProgressDialog mProgressDialog) {
        if (user != null) {
            String firstname, lastname;

            if (user.getDisplayName() != null) {
                String name[] = user.getDisplayName().split(" ", 2);
                if (name.length < 2) {
                    firstname = name[0];
                    lastname = name[0];
                } else {
                    firstname = name[0];
                    lastname = name[1];
                }
            } else {
                firstname = "twitter";
                lastname = "user";
            }
            String mPictureURL = null;
            if (user.getPhotoUrl() != null) {
                mPictureURL = user.getPhotoUrl().toString();
            }
            if (debug) {
                Log.d(TAG, "getUserDetails: email: " + user.getEmail());
            }
            mProgressDialog.dismiss();
            SocialLoginUser socialLoginUser = new SocialLoginUser(firstname, lastname, user.getEmail(), mPictureURL);
            FirebaseSignOut();
            mAuthTwitter.signOut();
            resultCallback.onResult(socialLoginUser);
        } else {
            if (debug) {
                Log.d(TAG, "getUserDetails: Firebase user returns null");
            }
            mProgressDialog.dismiss();
        }
    }
}
