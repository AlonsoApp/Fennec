package com.cloupix.fennec.business;

import java.io.UnsupportedEncodingException;

/**
 * Created by AlonsoUSA on 29/07/14.
 *
 */
public class CipheredContentC extends CipheredContent {

    public CipheredContentC(){
        super();
    }

    public CipheredContentC(byte[] fullContent){
        this.fullContent = fullContent;
    }


    @Override
    public void split() throws UnsupportedEncodingException {

    }
}
