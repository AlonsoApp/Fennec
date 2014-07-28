package com.cloupix.fennec.logic.security;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class SecurityManagerB extends SecurityManager {


    public SecurityManagerB(SecurityLevel securityLevel){
        this.securityLevel = securityLevel;
    }

    @Override
    public byte[] cipher(String msg) {
        return null;
    }

    @Override
    public byte[] cipher(byte[] content) {
        return content;
    }

    @Override
    public byte[] decipher(byte[] content) {
        return content;
    }

    @Override
    public String decipherToString(byte[] content) {
        return null;
    }

    @Override
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    @Override
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
}
