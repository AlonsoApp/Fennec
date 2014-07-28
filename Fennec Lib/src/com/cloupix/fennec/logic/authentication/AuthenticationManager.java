package com.cloupix.fennec.logic.authentication;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class AuthenticationManager {
    private static String authKey;

    public static String getAuthKey() {
        return authKey;
    }

    public static void setAuthKey(String authKey) {
        AuthenticationManager.authKey = authKey;
    }
}
