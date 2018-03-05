package com.webkul.sociallogin;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by aastha.gupta on 14/11/17 in prestashop_themes.
 */

public class Utils {
    public static int RC_GOOGLE_SIGN_IN = 9001;

    static void BundleLogger(Bundle bundle) {
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                String TAG = "Intent Logger";
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
    }
}
