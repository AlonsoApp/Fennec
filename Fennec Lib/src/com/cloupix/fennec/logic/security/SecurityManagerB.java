package com.cloupix.fennec.logic.security;

import com.cloupix.fennec.business.CipheredContent;
import com.cloupix.fennec.business.CipheredContentB;
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

        byte[] result = AES.encrypt(aesKey, content);


        result = concat(msgKey.getBytes(R.charset), result);
        result = concat(authKeySha.getBytes(R.charset), result);

        CipheredContentB cipheredContentB = new CipheredContentB();
        cipheredContentB.setAuthKeyShaLength(authKeySha.getBytes(R.charset).length);
        cipheredContentB.setMsgKeyLength(msgKey.getBytes(R.charset).length);
        cipheredContentB.setFullContent(result);

        return cipheredContentB;
    }

    public byte[] decipher(CipheredContent cipheredContent) throws GeneralSecurityException, UnsupportedEncodingException {
        CipheredContentB cipheredContentB = (CipheredContentB) cipheredContent;
        if(!cipheredContentB.isSplited())
            cipheredContentB.split();

        String msgKey = cipheredContentB.getMsgKey();

        String aesKey = getAesKey(msgKey);

        return AES.decrypt(aesKey, cipheredContentB.getContent());
    }


    private String getMsgKey(byte[] content) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return SHAsum(content);
    }

    private String getAesKey(String msgKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return SHAsum(concat(msgKey.getBytes(R.charset), authKey));
    }
}
