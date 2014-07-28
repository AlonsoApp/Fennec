package com.cloupix.fennec.business.exceptions;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class AuthenticationException extends SessionException {

    public static final int FORBIDDEN = 1;
    public static final int AUTH_PROCESS_FAILED = 2;

    private int type;

    public AuthenticationException(int type, String msg){
        super(msg);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
