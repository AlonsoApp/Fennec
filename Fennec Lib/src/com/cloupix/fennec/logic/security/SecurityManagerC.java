package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.CipheredContent;
import com.cloupix.fennec.business.CipheredContentC;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class SecurityManagerC extends SecurityManager {

    private String authKeyHex;

    public SecurityManagerC(SecurityLevel securityLevel){
        this.securityLevel = securityLevel;
    }

    @Override
    public CipheredContent cipher(byte[] content) throws GeneralSecurityException, UnsupportedEncodingException {

        return new CipheredContentC(AES.encrypt(byteArray2Hex(authKey), content));
    }

    @Override
    public byte[] decipher(CipheredContent cipheredContent) throws GeneralSecurityException, UnsupportedEncodingException {
        return AES.decrypt(byteArray2Hex(authKey), cipheredContent.getFullContent());
    }

    @Override
    public void setAuthKey(byte[] authKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        super.setAuthKey(authKey);
        this.authKeyHex = byteArray2Hex(authKey);
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
