package com.cloupix.fennec.business.exceptions;

/**
 * Created by AlonsoUSA on 16/07/14.
 *
 */
public class ProtocolException extends SessionException {


    public static final int BAD_IMPLEMENTED = 1;
    public static final int UNKNOWN_VERSION = 2;
    public static final int COMMAND_NOT_RECOGNIZED = 3;
    public static final int UNKNOWN_SECURITY_LEVEL = 2;

    private int type;

    public ProtocolException(int type, String msg){
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
