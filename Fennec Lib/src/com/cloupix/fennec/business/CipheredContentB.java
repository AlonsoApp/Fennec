package com.cloupix.fennec.business;

import com.cloupix.fennec.util.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by AlonsoUSA on 29/07/14.
 *
 */
public class CipheredContentB extends CipheredContent {

    private String msgKey;
    private int msgKeyLength;
    private byte[] content;

    private boolean splited = false;

    public String getMsgKey() {
        return msgKey;
    }

    public int getMsgKeyLenght() throws UnsupportedEncodingException {
        return msgKey != null ? msgKey.getBytes(R.charset).length : msgKeyLength;
    }

    public void setMsgKeyLength(int msgKeyLength) {
        this.msgKeyLength = msgKeyLength;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public CipheredContentB() {
    }

    public CipheredContentB(byte[] fullContent, int lenghtAuthSha, int lenghtMsgKey) throws UnsupportedEncodingException {
        this.fullContent = fullContent;
        split(fullContent, lenghtAuthSha, lenghtMsgKey);
    }

    @Override
    public void split() throws UnsupportedEncodingException {
        split(fullContent, authKeyShaLength, msgKeyLength);
    }

    public void split(byte[] fullContent, int lenghtAuthSha, int lenghtMsgKey) throws UnsupportedEncodingException {

        byte[] authKeyBytes = new byte[lenghtAuthSha];
        System.arraycopy(fullContent, 0, authKeyBytes, 0, authKeyBytes.length);
        this.authKeySha = new String(authKeyBytes, R.charset);

        byte[] msgKeyBytes = new byte[lenghtAuthSha];
        System.arraycopy(fullContent, lenghtAuthSha, msgKeyBytes, 0, msgKeyBytes.length);
        this.msgKey = new String(msgKeyBytes, R.charset);

        byte[] content = new byte[fullContent.length - (lenghtAuthSha + lenghtMsgKey)];
        System.arraycopy(fullContent, (lenghtAuthSha + lenghtMsgKey), content, 0, content.length);
        this.content = content;
        splited = true;
    }

    private void fill(int from, int to, byte[] emptyContainer, byte[] fullContainer){
        for(int i =from; i<to; i++){
            emptyContainer[i] = fullContainer[i];
        }
    }

    public boolean isSplited() {
        return splited;
    }
}
