package com.cloupix.fennec.business;

import com.cloupix.fennec.util.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by AlonsoUSA on 29/07/14.
 *
 */
public class CipheredContent {


    protected String authKeySha;
    protected int authKeyShaLength;

    protected byte[] fullContent;



    public byte[] getFullContent() {
        return fullContent;
    }

    public void setFullContent(byte[] content) {
        this.fullContent = content;
    }


    public String getAuthKeySha() throws UnsupportedEncodingException {
        if(authKeySha==null && fullContent!=null && authKeyShaLength>0)
            split();
        return authKeySha;
    }

    public int getAuthKeyShaLength() throws UnsupportedEncodingException {

        return authKeySha != null? authKeySha.getBytes(R.charset).length : authKeyShaLength;
    }

    public void setAuthKeyShaLength(int authKeyShaLength) {
        this.authKeyShaLength = authKeyShaLength;
    }

    public void setAuthKeySha(String authKeySha) {
        this.authKeySha = authKeySha;
    }


    public void split() throws UnsupportedEncodingException{

    }
}
