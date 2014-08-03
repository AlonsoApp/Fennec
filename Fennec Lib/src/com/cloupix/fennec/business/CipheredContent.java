package com.cloupix.fennec.business;

import com.cloupix.fennec.util.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by AlonsoUSA on 29/07/14.
 *
 */
public class CipheredContent {

    public static final int CLASS_A = 1;
    public static final int CLASS_B = 2;

    private int cipherClass;

    private String authKeySha;
    private int authKeyShaLength;

    private byte[] fullContent;
    private boolean splited = false;

    private String msgKey;
    private int msgKeyLength;
    private byte[] content;


    

    public CipheredContent(int cipherClass){
        this.cipherClass = cipherClass;
    }

    public CipheredContent(byte[] fullContent){
        this.cipherClass = CLASS_A;
        this.fullContent = fullContent;
    }

    public int getCipherClass() {
        return cipherClass;
    }

    public void setCipherClass(int cipherClass) {
        this.cipherClass = cipherClass;
    }

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
        this.cipherClass = CLASS_B;
    }

    public void setAuthKeySha(String authKeySha) {
        this.authKeySha = authKeySha;
        this.cipherClass = CLASS_B;
    }


    public void split() throws UnsupportedEncodingException{
        split(fullContent, authKeyShaLength, msgKeyLength);
    }

    public String getMsgKey() {
        return msgKey;
    }

    public int getMsgKeyLenght() throws UnsupportedEncodingException {
        return msgKey != null ? msgKey.getBytes(R.charset).length : msgKeyLength;
    }

    public void setMsgKeyLength(int msgKeyLength) {
        this.msgKeyLength = msgKeyLength;
        this.cipherClass = CLASS_B;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
        this.cipherClass = CLASS_B;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
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
