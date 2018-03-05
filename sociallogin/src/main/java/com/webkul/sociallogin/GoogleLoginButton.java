package com.webkul.sociallogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aastha.gupta on 11/11/17 in prestashop_themes.
 */

public class GoogleLoginButton extends android.support.v7.widget.AppCompatImageButton {
    private FirebaseAuth mAuth;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;
    private Activity activityContext;
    private SocialLoginCallback resultCallback;
    private boolean debug;
    private boolean isInitialized;
    private String TAG = "CustomGoogleLogin";

    public GoogleLoginButton(Context context) {
        this(context, null);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

//    public void onStart(){
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        getUserDetails(currentUser);
//        if (mGoogleApiClient == null) {
//            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                    .requestIdToken(getString(R.string.default_web_client_id))
//                    .requestEmail()
//                    .build();
//            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
////                    .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
////                        @Override
////                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
////
////                        }
////                    })
//                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                    .build();
//        } else if (!mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        }
//    }

    public void initialize(Activity activity, String defaultWebClientID, SocialLoginCallback resultCallback, boolean debug) {
        this.resultCallback = resultCallback;
        this.debug = debug;
        mAuth = FirebaseAuth.getInstance();
        activityContext = activity;
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        getUserDetails(currentUser, acct.getEmail());
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(defaultWebClientID)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
//                    .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
//                        @Override
//                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//                        }
//                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } else if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        isInitialized = true;
        if (debug) {
            Log.d(TAG, "initializeGoogle: initialization started");
        }
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
//        setImageResource(R.drawable.ic_vector_google_plus);
//        setBackgroundColor(Color.TRANSPARENT);
//        setPadding(10, 10, 10, 10);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInitialized) {
                    if (debug) {
                        Log.d(TAG, "onClick: Google Login Started");
                    }
                    mProgressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.please_wait), getResources().getString(R.string.processing_request_response), true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    mProgressDialog.dismiss();
                    activityContext.startActivityForResult(signInIntent, Utils.RC_GOOGLE_SIGN_IN);
                } else {
                    Log.e("SOCIAL LOGIN", "GOOGLE NOT INITIALIZED");
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (debug) {
            Log.d(TAG, "onActivityResult: resultCode " + resultCode);
        }
        if (resultCode == RESULT_OK) {
            mProgressDialog.show();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                if (opr != null) {
                    if (opr.isDone()) {
                        GoogleSignInResult result = opr.get();
//                    handleSignInResult(result);
                    } else {
                        opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                            @Override
                            public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
//                           handleSignInResult(googleSignInResult);
                            }
                        });
                    }
                }
            }
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                if (debug) {
                    Log.d(TAG, "onActivityResult: Google login successful");
                }
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                if (debug) {
                    Log.d(TAG, "onActivityResult: Google login failed");
                }
                mProgressDialog.dismiss();
            }
        } else {
            FirebaseSignOut();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        try {
            if (debug) {
                Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            }
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(((AppCompatActivity) mContext), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                getUserDetails(user, acct.getEmail());
                            }
                        }
                    });
        } catch (RuntimeExecutionException e) {
            e.printStackTrace();
//            Crashlytics.logException(e);
            FirebaseSignOut();
            resultCallback.onError();
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getUserDetails(FirebaseUser user, String email) {
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
                firstname = "google";
                lastname = "user";
            }
            String mPictureURL = null;
            if (user.getPhotoUrl() != null) {
                mPictureURL = user.getPhotoUrl().toString();
            }
            mProgressDialog.dismiss();
            String emailAddress;
            if (user.getEmail() == null) {
                emailAddress = email;
            } else {
                emailAddress = user.getEmail();
            }
            SocialLoginUser socialLoginUser = new SocialLoginUser(firstname, lastname, emailAddress, mPictureURL);

            if (debug) {
                Log.d(TAG, "onSuccess: " + socialLoginUser.toString());
            }
            FirebaseSignOut();
            resultCallback.onResult(socialLoginUser);
        }
    }

    private void FirebaseSignOut() {
        mAuth.signOut();
        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
