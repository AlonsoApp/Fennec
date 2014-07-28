package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.exceptions.ProtocolException;

import java.io.UnsupportedEncodingException;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public abstract class SecurityManager {


    protected SecurityLevel securityLevel;


    public static SecurityManager build(SecurityLevel securityLevel) throws ProtocolException {

        if(securityLevel.getSecurityClass().equals("A")){
            return new SecurityManagerA(securityLevel);
        }else if(securityLevel.getSecurityClass().equals("B")){
            return new SecurityManagerB(securityLevel);
        }else if(securityLevel.getSecurityClass().equals("C")){
            return new SecurityManagerC(securityLevel);
        }else{
            throw new ProtocolException(ProtocolException.UNKNOWN_SECURITY_LEVEL, "Unknown security level " + securityLevel);
        }
    }

    public abstract byte[] cipher(String content);

    public abstract byte[] cipher(byte[] content);

    public abstract byte[] decipher(byte[] content);

    public abstract String decipherToString(byte[] content) throws UnsupportedEncodingException;

    public abstract SecurityLevel getSecurityLevel();

    public abstract void setSecurityLevel(SecurityLevel securityLevel);

}
