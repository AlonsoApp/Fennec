package com.cloupix.fennec.business;


import java.io.UnsupportedEncodingException;

/**
 * Created by AlonsoUSA on 29/07/14.
 *
 */
public class CipheredContentA extends CipheredContent {

    public CipheredContentA(){
        super();
    }

    public CipheredContentA(byte[] fullContent){
        this.fullContent = fullContent;
    }


    @Override
    public void split() throws UnsupportedEncodingException {

    }
}
