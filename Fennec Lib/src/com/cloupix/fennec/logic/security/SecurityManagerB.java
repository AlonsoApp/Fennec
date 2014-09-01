package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.CipheredContent;
import com.cloupix.fennec.util.R;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by AlonsoUSA on 21/07/14.
 *
 */
public class SecurityManagerB extends SecurityManager {


    public SecurityManagerB(SecurityLevel securityLevel){
        this.securityLevel = securityLevel;
    }

    @Override
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    @Override
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }


    @Override
    public CipheredContent cipher(byte[] content) throws GeneralSecurityException, UnsupportedEncodingException {
        String msgKey = getMsgKey(content);

        String aesKey = getAesKey(msgKey);

        byte[] result = AESCipher.cipher(aesKey, content);


        result = concat(msgKey.getBytes(R.charset), result);
        result = concat(authKeySha.getBytes(R.charset), result);

        CipheredContent cipheredContent = new CipheredContent(CipheredContent.CLASS_B);
        cipheredContent.setAuthKeyShaLength(authKeySha.getBytes(R.charset).length);
        cipheredContent.setMsgKeyLength(msgKey.getBytes(R.charset).length);
        cipheredContent.setFullContent(result);

        return cipheredContent;
    }

    public byte[] decipher(CipheredContent cipheredContent) throws GeneralSecurityException, UnsupportedEncodingException {
        CipheredContent cipheredContentAB = (CipheredContent) cipheredContent;
        if(!cipheredContentAB.isSplited())
            cipheredContentAB.split();

        String msgKey = cipheredContentAB.getMsgKey();

        String aesKey = getAesKey(msgKey);

        return AESCipher.decipher(aesKey, cipheredContentAB.getContent());
    }


    private String getMsgKey(byte[] content) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return SHAsum(content);
    }

    private String getAesKey(String msgKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return SHAsum(concat(msgKey.getBytes(R.charset), authKey));
    }
}
