package com.cloupix.fennec.business.exceptions;

/**
 * Created by AlonsoUSA on 16/07/14.
 *
 */
public class SessionException extends Exception{

    public static final String CONNECTION_EXCEPTION = "Connection exception";
    public static final String HELLO_EXCEPTION = "Protocol version negotiation exception";

    public SessionException(){

    }

    public SessionException(String msg){
        super(msg);
    }

}
