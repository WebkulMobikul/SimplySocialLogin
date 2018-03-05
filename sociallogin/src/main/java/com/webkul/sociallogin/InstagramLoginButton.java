package com.webkul.sociallogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.webkul.sociallogin.instagram.InstagramApp;

import java.util.HashMap;

/**
 * Created by aastha.gupta on 11/11/17 in prestashop_themes.
 * A custom view to show Instagram Button
 */

public class InstagramLoginButton extends android.support.v7.widget.AppCompatImageButton {
    private InstagramApp mInstagramApp;
    private SocialLoginCallback resultCallback;
    private Context mContext;
    private String TAG = "InstagramLoginButton";
    private boolean isInitialized;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            ProgressDialog mProgressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.please_wait),
                    getResources().getString(R.string.processing_request_response), true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                HashMap<String, String> mInstagramUserInfoHashmap = mInstagramApp.getUserInfo();
                mInstagramApp.resetAccessToken();
                getEmail(mInstagramUserInfoHashmap, mProgressDialog);
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            } else {
                mProgressDialog.dismiss();
            }
            return false;
        }
    });
    private boolean debug;

    public InstagramLoginButton(Context context) {
        this(context, null);
    }

    public InstagramLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InstagramLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void initialize(final Context context, String clientID, String clientSecret,
                           String redirectURL, SocialLoginCallback resultCallback, boolean debug) {
        this.debug = debug;
        mInstagramApp = new InstagramApp(context, clientID, clientSecret, redirectURL);
        this.resultCallback = resultCallback;
        mInstagramApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                mInstagramApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        isInitialized = true;
        if (debug) {
            Log.d(TAG, "Instagram : Initialization done ");
        }
    }

    private void init() {
//        setImageResource(R.drawable.ic_vector_instagram);
//        setBackgroundColor(Color.TRANSPARENT);
//        setPadding(10, 10, 10, 10);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInitialized) {
                    if (mInstagramApp.hasAccessToken()) {
                        mInstagramApp.resetAccessToken();
                    } else {
                        mInstagramApp.authorize();
                    }
                } else {
                    Log.e("SOCIAL LOGIN", "INSTAGRAM NOT INITIALIZED");
                }
            }
        });
    }

    private void getEmail(final HashMap<String, String> parameters, final ProgressDialog mProgressDialog) {
        final View emailAddressView = LayoutInflater.from(mContext).inflate(R.layout.enter_email_address, null);
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("Email required").setView(emailAddressView)
                .setPositiveButton(mContext.getString(R.string.apply),
                        null
                ).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String email = ((EditText) emailAddressView.findViewById(R.id.insta_email_id)).getText().toString().trim();
                        if (email.isEmpty()) {
                            ((EditText) emailAddressView.findViewById(R.id.insta_email_id)).setError(mContext.getString(R.string.required_field));
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            ((EditText) emailAddressView.findViewById(R.id.insta_email_id)).setError(mContext.getString(R.string.enter_valid_email));
                        } else {
                            ((EditText) emailAddressView.findViewById(R.id.insta_email_id)).setError(null);
                            String firstname, lastname;
                            String name[] = parameters.get(InstagramApp.TAG_FULL_NAME).split(" ", 2);
                            if (name.length < 2) {
                                firstname = name[0];
                                lastname = name[0];
                            } else {
                                firstname = name[0];
                                lastname = name[1];
                            }
                            String mPictureURL = null;
                            if (parameters.get(InstagramApp.TAG_PROFILE_PICTURE) != null) {
                                mPictureURL = parameters.get(InstagramApp.TAG_PROFILE_PICTURE);
                            }
                            mProgressDialog.dismiss();
                            dialog.dismiss();
                            SocialLoginUser socialLoginUser = new SocialLoginUser(firstname, lastname, email, mPictureURL);
                            resultCallback.onResult(socialLoginUser);
                        }
                    }
                });
            }
        });
        dialog.show();
    }
}
