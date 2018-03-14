# SimplySocialLogin
A library to make social login easy in android.

:star:  `build support version used : 26.1.0 `

# Importing Istructions

Add this line in your `module level build.gradle` file

```
  compile 'com.webkul.mobikul:simply-social-login:1.0.1'
```

# Usage Examples

*      Facebook Login
```
  <com.webkul.sociallogin.CustomFacebookLoginButton
       android:id="@+id/facebook_login"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:padding="10dp"
       app:srcCompat="@drawable/ic_vector_facebook" />
```

*      Google Login
```
  <com.webkul.sociallogin.GoogleLoginButton
       android:id="@+id/google_sign_in_button"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:padding="10dp"
       app:srcCompat="@drawable/ic_vector_google_plus" />
```
*      Instagram Login
```
  <com.webkul.sociallogin.InstagramLoginButton
       android:id="@+id/instagram_sign_in_button"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:padding="10dp"
       app:srcCompat="@drawable/ic_vector_instagram" />
```
*      Twitter Login
```
  <com.webkul.sociallogin.CustomTwitterButton
       android:id="@+id/twitter_login_button"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:layout_margin="10dp"
       android:background="@android:color/transparent"
       app:icon="@drawable/ic_vector_twitter"
       app:label="@string/empty"
       app:labelColor="@color/tw__composer_white" />
```

# Initialization

There are different initialization method ```initialize()``` use them to initialize each and every library with the parameters needed.

        

        

        