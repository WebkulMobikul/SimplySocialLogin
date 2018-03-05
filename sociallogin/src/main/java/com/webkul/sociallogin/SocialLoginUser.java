package com.webkul.sociallogin;

/**
 * Created by aastha.gupta on 11/11/17 in prestashop_themes.
 */

public class SocialLoginUser {
    private String firstName, lastName, email, profilePictureURL;

    public SocialLoginUser(String firstName, String lastName, String email, String profilePictureURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePictureURL = profilePictureURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    @Override
    public String toString() {
        return "User : { firstname : " + firstName + " ,lastname : " + lastName + " ,email : " +
                email + " , profile_picture : " + profilePictureURL + "}";
    }
}
